package experiments;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.mining.geneticmining.fitness.duplicates.DTImprovedContinuousSemanticsFitness;
import org.processmining.mining.heuristicsmining.HeuristicsMiner;
import org.processmining.mining.heuristicsmining.HeuristicsMinerGUI;
import org.processmining.mining.heuristicsmining.HeuristicsMinerParameters;
import org.processmining.mining.heuristicsmining.HeuristicsNetResult;
import org.processmining.mining.petrinetmining.AlphaPPProcessMiner;
import org.processmining.mining.petrinetmining.AlphaProcessMiner;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.processmining.mining.petrinetmining.TsinghuaAlphaProcessMiner;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.kutoolbox.groupedlog.GroupedXLog;
import org.processmining.plugins.kutoolbox.logmappers.PetrinetLogMapper;
import org.processmining.plugins.kutoolbox.utils.ImportUtils;
import org.processmining.plugins.kutoolbox.utils.LogUtils;
import org.processmining.plugins.neconformance.PetrinetEvaluatorPlugin;

import savers.Petrinet5ToPnml;
import runner.AbstractExperimentRunner;
import utils.ResultsTable;
import be.kuleuven.econ.cbf.utils.log.LogReaderFacade;

public class SingleFittingTraceMetricExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, final String[] configuration,
			ResultsTable results) {
		
		// Do not even try to comprehend the reason we use a multi-threaded,
		// multi-process approach here. Is there a reason? Yes.
		// Multi-proc: otherwise too many awt windows in prom5 crash everything.
		// Multi-thread: to at least keep it a bit fast.
		
		final GroupedXLog gLog = new GroupedXLog(log);
		double[] good = new double[4];
		
		final List<double[]> resultvals = new ArrayList<double[]>();
		BlockingQueue<Runnable> worksQueue;
		ThreadPoolExecutor executor;
		worksQueue = new ArrayBlockingQueue<Runnable>(10);
		executor = new ThreadPoolExecutor(
				12, 	// core size
				12, 	// max size
				10, 	// keep alive time
				TimeUnit.MINUTES, 	// keep alive time units
				worksQueue 			// the queue to use
		);
		
		for (int t = 0; t < gLog.size(); t++) {
			double[] g = new double[4];
			resultvals.add(g);
		}
		
		for (int t = 0; t < gLog.size(); t++) {
			final int tt = t;
			final double s = (double) gLog.get(t).size();
			while (executor.getQueue().remainingCapacity() == 0);
			executor.execute(new Runnable(){
				@Override
				public void run() {
					double[] g = new double[4];
					String savePath = getTraceHash(gLog.get(tt).get(0));
					System.err.println(savePath);
					g[0] = s * runTrace(savePath, configuration, 0);
					g[1] = s * runTrace(savePath, configuration, 1);
					g[2] = s * runTrace(savePath, configuration, 2);
					g[3] = s * runTrace(savePath, configuration, 3);
					resultvals.set(tt, g);
				}
			});
		}

		executor.shutdown();
		try {
			while (!executor.awaitTermination(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int t = 0; t < gLog.size(); t++) {
		//	double s = (double) gLog.get(t).size();
			good[0] += resultvals.get(t)[0];
			good[1] += resultvals.get(t)[1];
			good[2] += resultvals.get(t)[2];
			good[3] += resultvals.get(t)[3];
		}
			
		results.put("alpha", good[0] / (double)log.size());
		results.put("alphaPP", good[1] / (double)log.size());
		results.put("alphaT", good[2] / (double)log.size());
		results.put("hm5low", good[3] / (double)log.size());

	}
	
	private String getTraceHash(XTrace trace) {
		String th = "";
		for (XEvent e : trace)
			th += "#####"+XConceptExtension.instance().extractName(e);
		return th;
	}
	
	private double runTrace(String savePath, String[] configuration, int num) {
		double good = 0;
		Process p = null;
		try {
			p = createProcess(savePath, configuration, num);
			ObjectInputStream ois = new ObjectInputStream(p.getInputStream());
			good = ois.readDouble();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println(" ---> "+num);
			good = 0;
		}
		
		return good;
	}

	private Process createProcess(String savePath, String[] configuration, int num) throws IOException {
		// program command:
		// [java.home]/bin/java -cp [java.class.path] Main
		for (int s = 0; s < configuration.length; s++)
			if (configuration[s] == null)
				configuration[s] = "NULL";
		String separator = System.getProperty("file.separator");
		String executable =
		System.getProperty("java.home")
				+ separator + "bin"
				+ separator + "java";
		String classpath = System.getProperty("java.class.path");
		String main = ExperimentProcess.class.getCanonicalName();
		String[] command = new String[] { executable, "-Xmx512M", "-cp", classpath, main };
		String[] fullCommand = concat(command, configuration);
		fullCommand = concat(fullCommand, new String[]{savePath, ""+num});
		return Runtime.getRuntime().exec(fullCommand);
	}

	private static double getReplayResult(XLog log, PetriNet net, String[] configuration) {
		Petrinet5ToPnml saver = new Petrinet5ToPnml();
		saver.setSaveObject(net);
		String savePath = configuration[3] + 
				new File(configuration[0]).getName().replace(".xes", "") + ".tmp"+System.currentTimeMillis()+".pnml";
		System.out.println("Saving to: "+savePath);
		saver.save(new File(savePath));
		Petrinet newNet = ImportUtils.openPetrinet(new File(savePath));
		PetrinetLogMapper mapper = PetrinetLogMapper.getStandardMap(log, newNet);
		System.out.println("Running...");
		double recall = PetrinetEvaluatorPlugin.getMetricValue(
				log, newNet, mapper, 
				false, false, true, 
				false, false, 
				-1, -1, 
				true, true, true, 
				false, "recall");
		System.out.println("Done");
		
		return recall;
	}
	
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static double calculateAlpha(XLog newLog, String[] configuration) {
		LogReaderFacade logReader = new LogReaderFacade(newLog);
		AlphaProcessMiner miner = new AlphaProcessMiner();
		PetriNetResult result = (PetriNetResult) miner.mine(logReader);
		PetriNet net = result.getPetriNet();
		double r = getReplayResult(newLog, net, configuration);	
		if (r >= 1D) return 1D;
		return 0D;
	}

	public static double calculateAlphaT(XLog newLog, String[] configuration) {
		LogReaderFacade logReader = new LogReaderFacade(newLog);
		TsinghuaAlphaProcessMiner miner = new TsinghuaAlphaProcessMiner();
		miner.getOptionsPanel(logReader.getLogSummary());
		PetriNetResult result = (PetriNetResult) miner.mine(logReader);
		PetriNet net = result.getPetriNet();
		double r = getReplayResult(newLog, net, configuration);	
		if (r >= 1D) return 1D;
		return 0D;
	}

	public static double calculateAlphaPP(XLog newLog, String[] configuration) {
		LogReaderFacade logReader = new LogReaderFacade(newLog);
		AlphaPPProcessMiner miner = new AlphaPPProcessMiner();
		PetriNetResult result = (PetriNetResult) miner.mine(logReader);
		PetriNet net = result.getPetriNet();
		double r = getReplayResult(newLog, net, configuration);	
		if (r >= 1D) return 1D;
		return 0D;
	}

	public static double calculateHeuristicsMiner5Low(XLog newLog, String[] configuration) {
		LogReaderFacade logReader = new LogReaderFacade(newLog);
		HeuristicsMiner miner = new HeuristicsMiner();
		HeuristicsMinerParameters parameters = new HeuristicsMinerParameters();
		parameters.setDependencyThreshold(.0);
		parameters.setL1lThreshold(.0);
		parameters.setL2lThreshold(.0);
		parameters.setPositiveObservationsThreshold(1);
		HeuristicsMinerGUI panel = (HeuristicsMinerGUI) miner.getOptionsPanel(logReader.getLogSummary());
		panel.setHeuristicsMinerParameters(parameters);
		HeuristicsNetResult result = (HeuristicsNetResult) miner.mine(logReader);
		HeuristicsNet[] array = new HeuristicsNet[] { result.getHeuriticsNet() };
		DTImprovedContinuousSemanticsFitness fitness = new DTImprovedContinuousSemanticsFitness(logReader);
		fitness.calculate(array);
		if (array[0].getFitness() >= 1D) return 1D;
		return 0D;
	}

}

class ExperimentProcess extends AbstractExperimentRunner {
	public static void main(String[] configuration) {
		ObjectOutputStream oos = null;
		try {
			System.in.close();
			oos = new ObjectOutputStream(System.out);
			System.setOut(new PrintStream(new NullOutputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		String savePath = configuration[configuration.length-2];
		int num = Integer.parseInt(configuration[configuration.length-1]);
		
	//	XLog newLog = ImportUtils.openLog(new File(savePath));
		XLog newLog = LogUtils.newLog("new");
		XTrace trace = new XTraceImpl(new XAttributeMapImpl());
		XConceptExtension.instance().assignName(trace, "trace");
		String[] evts = savePath.split("#####");
		for (String e : evts) {
			if (e.equals("")) continue;
			XEvent ev = new XEventImpl();
			XConceptExtension.instance().assignName(ev, e);
			XLifecycleExtension.instance().assignStandardTransition(ev, StandardModel.COMPLETE);
			trace.add(ev);
		}
		newLog.add(trace);
		
		try {
			double good = 0d;
			if (num == 0) good = SingleFittingTraceMetricExperiment.calculateAlpha(newLog, configuration);
			if (num == 1) good = SingleFittingTraceMetricExperiment.calculateAlphaPP(newLog, configuration);
			if (num == 2) good = SingleFittingTraceMetricExperiment.calculateAlphaT(newLog, configuration);
			if (num == 3) good = SingleFittingTraceMetricExperiment.calculateHeuristicsMiner5Low(newLog, configuration);
			
			oos.writeDouble(good);
			oos.flush();
			
			// Clean up
			oos.flush();
			System.err.write('\0');
			System.err.flush();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

class NullOutputStream extends OutputStream {
	@Override
	public void write(int arg0) throws IOException {
	}
}
