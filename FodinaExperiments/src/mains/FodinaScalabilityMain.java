package mains;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.nikefsmonitor.NikeFSMonitor;

import runner.CSVExperimentRunner;

public class FodinaScalabilityMain {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\scalability\\";
	public static final String outputPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String experimentResultPath = outputPath+"allscalability.csv";
	
	public static void main(String[] args) {
		NikeFSMonitor monitor = new NikeFSMonitor();
		monitor.setSwapDir("F:\\NIKEFS2\\");
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(150000000);
		
		List<String[]> configurations = new ArrayList<String[]>();
		int repeats = 20;
		int[] distinctSizes = new int[]{5, 10, 50, 100, 200, 300, 400, 500, 1000, 2500, 5000};
		int[] activitiesNum = new int[]{2, 3, 5, 10, 15, 20, 25};
		int[] completeSizes = new int[]{10, 100, 250, 500, 750, 1000, 2500, 5000, 10000};
		int[] traceSizes    = new int[]{3, 5, 10, 15, 20, 25, 50};
		String[] algos		= new String[]{"fodina", "hm6", "fhm6"};
		for (int ds : distinctSizes) {
		for (int an : activitiesNum) {
		for (int cs : completeSizes) {
		if (cs < ds) continue;
		for (int ts : traceSizes) {
		for (int r = 1; r <= repeats; r++) {
		for (String a : algos) {
				String[] config = new String[11];
				config[0] = createLog(ds, cs, ts, an, r);
				config[1] = null;
				config[2] = "experiments.FodinaScalabilityExperiment";
				config[3] = outputPath;
				config[4] = a;
				config[5] = "dis s: "+ds;
				config[6] = "com s: "+cs;
				config[7] = "tra s: "+ts;
				config[8] = "act n: "+an;
				config[9] = "r: "+r;
				config[10] = "algo: "+a;
				configurations.add(config);
				System.out.println(config[0]);
		} } } } } }
		
		
		
		CSVExperimentRunner runner = new CSVExperimentRunner(experimentResultPath);
		runner.runExperiments(configurations);
	}

	private static String createLog(int ds, int cs, int ts, int an, int r) {
		Set<String[]> distincts = new HashSet<String[]>();
		Random ra = new Random();
		while (distincts.size() < ds) {
			int s = ts + ra.nextInt(ts/2) - ts/2;
			String[] trace = createTrace(s, an);
			distincts.add(trace);
		}
		
		XLog log = XFactoryRegistry.instance().currentDefault().createLog();
		for (String[] dt : distincts) {
			log.add(traceFromArray(dt));
		}
		
		List<String[]> distinctsList = new ArrayList<String[]>(distincts);
		while (log.size() < cs) {
			log.add(traceFromArray(distinctsList.get(ra.nextInt(distinctsList.size()))));
		}
		
		String name = basePath + ds+"-"+cs+"-"+ts+"-"+an+"--"+r+".xes";
		try {
			ExportUtils.exportLog(log, new File(name));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return name;
	}
	
	private static XTrace traceFromArray(String[] dt) {
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace();
		for (String e : dt) {
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent();
			XConceptExtension.instance().assignName(event, e);
			XLifecycleExtension.instance().assignStandardTransition(event, StandardModel.COMPLETE);
			trace.add(event);
		}
		return trace;
	}

	private static String[] createTrace(int size, int an) {
		String[] trace = new String[2+size];
		Random r = new Random();
		for (int i = 1; i <= size; i++)
			trace[i] = activityFromNumber(1+r.nextInt(an));
		trace[0] = "start";
		trace[trace.length-1] = "end";
		return trace;
	}
	
	public static String activityFromNumber(int column) {
		String columnString = "";
		int columnNumber = column;
		while (columnNumber > 0) {
			int currentLetterNumber = (columnNumber - 1) % 26;
			char currentLetter = (char) (currentLetterNumber + 65);
			columnString = currentLetter + columnString;
			columnNumber = (columnNumber - (currentLetterNumber + 1)) / 26;
		}
		return columnString;
	}
}
