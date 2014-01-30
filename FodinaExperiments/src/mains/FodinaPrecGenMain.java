package mains;

import it.unipd.math.plg.models.PlgObservation;
import it.unipd.math.plg.models.PlgParameters;
import it.unipd.math.plg.models.PlgProcess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;
import org.processmining.lib.mxml.LogException;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.LogUtils;
import org.processmining.plugins.nikefsmonitor.NikeFSMonitor;

import runner.CSVExperimentRunner;

public class FodinaPrecGenMain {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\precgen\\";
	public static final String outputPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String experimentResultPath = outputPath + "fodinaprecgen-rep.csv";

	public static void main(String[] args) {
		NikeFSMonitor monitor = new NikeFSMonitor();
		monitor.setSwapDir("F:\\NIKEFS2\\");
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(100000000);

		List<String[]> configurations = new ArrayList<String[]>();
		
		double[] configs = new double[] {
				-1, -.9, -.8, -.7, -.6, -.5, -.4, -.3, -.2, -.1,
				0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1 };
		
		/*
		int modelsToTest = 20;
		for (int im = 0; im < modelsToTest; im++) {
			System.out.println("------------------------------");
			System.out.println("----------- " + im + " -----------");
			System.out.println("------------------------------");
			String logName = createLog(im, 100, 3, 10);
			for (double d : configs) {
				String[] config = new String[7];
				config[0] = logName;
				config[1] = null;
				config[2] = "experiments.FodinaPrecGenExperiment";
				config[3] = outputPath;
				config[4] = "model num: " + im;
				config[5] = "slider par: " + d;
				config[6] = "" + d;
				configurations.add(config);
			}
		}*/
		
		int[][] logs = new int[][]{
				new int[]{2,2},
				new int[]{2,3},
				new int[]{3,3},
				new int[]{2,5},
				new int[]{3,5},
				new int[]{5,5},
				new int[]{2,10},
				new int[]{3,10}
		};
		
		for (int[] c : logs) {
			System.out.println("------------------------------");
			System.out.println("----------- " + c[0] + " -----------");
			System.out.println("----------- " + c[1] + " -----------");
			System.out.println("------------------------------");
			String logName = createLog(c[0], getLetters(c[1]));
			for (double d : configs) {
				String[] config = new String[7];
				config[0] = logName;
				config[1] = null;
				config[2] = "experiments.FodinaPrecGenExperiment";
				config[3] = outputPath;
				config[4] = "size: " + c[0];
				config[5] = "acts: " + c[1];
				config[6] = "" + d;
				configurations.add(config);
			}
		}

		CSVExperimentRunner runner = new CSVExperimentRunner(experimentResultPath);
		runner.runExperiments(configurations);
	}
	
	private static String[] getLetters(int size) {
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		String[] s = new String[size];
		for (int l = 0; l < size; l++)
			s[l] = alphabet.substring(l, l+1);
		return s;
	}

	private static String createLog(int length, String[] activities) {
		String name = basePath + "log-" + length + "_" + activities.length + ".xes";
		
		XLog log = LogUtils.newLog("Generated log "+length+" "+activities.length);
		
		String[] trace = new String[length];
		recurse(log, trace, 0, length, activities);
		
		try {
			ExportUtils.exportLog(log, new File(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return name;
	}

	private static void recurse(XLog log, String[] trace, int pos, int length, String[] activities) {
		if (pos == length) {
			XTrace xtrace = new XTraceImpl(new XAttributeMapImpl());
			XConceptExtension.instance().assignName(xtrace, "Trace");
			xtrace.add(makeEvent("__start__"));
			for (String a : trace)
				xtrace.add(makeEvent(a));
			xtrace.add(makeEvent("__end__"));
			log.add(xtrace);
		} else {
			for (String a : activities) {
				trace[pos] = a;
				recurse(log, trace, pos+1, length, activities);
			}
		}
	}

	@SuppressWarnings("unused")
	private static String createLog(int i, int size, int depth, int noise) {
		XLog log = LogUtils.newLog("Generated log "+i);
		String name = basePath + "log-" + i + ".xes";
		
	//	if (i < 52) return name; // These are done already
		
		try {
			PlgProcess p = null;
			do {
				try {
					p = new PlgProcess("random process");
					p.randomize(depth);
				//	p.savePetriNetAsTPN(basePath + "model-" + i + ".tpn");
				} catch (Exception e) {
					System.out.println("Restarting...");
					p = null;
				}
			} while (p == null);

			for (int l = 0; l < size; l++) {
				try {
					log.add(makeTrace(p, noise));
				} catch (LogException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ExportUtils.exportLog(log, new File(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return name;
	}
	
	private static XTrace makeTrace(PlgProcess process, int percentErrors)
			throws IOException, LogException {

		XTrace xtrace = new XTraceImpl(new XAttributeMapImpl());
		XConceptExtension.instance().assignName(xtrace, "Trace");
		xtrace.add(makeEvent("__start__"));

		percentErrors = (percentErrors < 0) || (percentErrors > 100)
				? 0
				: percentErrors;

		Vector<PlgObservation> v = process.getFirstActivity().generateInstance(0);

		for (int i = 0; i < v.size(); i++) {
			if (PlgParameters.randomFromPercent(percentErrors)) {
				PlgObservation o1 = (PlgObservation) v.get(i);
				int randomIndex;
				do {
					randomIndex = PlgProcess.generator.nextInt(v.size());
				} while (randomIndex == i);
				PlgObservation o2 = (PlgObservation) v.get(randomIndex);

				int o1StartingTime = o1.getStartingTime();
				o1.setStartingTime(o2.getStartingTime());
				o2.setStartingTime(o1StartingTime);
			}
			PlgObservation o = (PlgObservation) v.get(i);
			xtrace.add(makeEvent(o.getActivity().getName()));
		}

		xtrace.add(makeEvent("__end__"));

		return xtrace;
	}

	private static XEvent makeEvent(String name) {
		XEvent event = new XEventImpl();
		XConceptExtension.instance().assignName(event, name);
		XLifecycleExtension.instance().assignTransition(event, "complete");
		return event;
	}
}
