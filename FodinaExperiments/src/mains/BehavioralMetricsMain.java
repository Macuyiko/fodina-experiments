package mains;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

import runner.CSVExperimentRunner;

public class BehavioralMetricsMain {
	public static final String logPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\";
	public static final String modelPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String experimentResultPath = logPath+"behavioralmetrics.csv";
	
	public static void main(String[] args) {
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(200000000);
		
		File modelFile = new File(modelPath);
		File logFile = new File(logPath);
		List<String[]> configurations = new ArrayList<String[]>();
		
		for (File dirFile : modelFile.listFiles()) {
			if (!dirFile.isDirectory()) continue;
			for (File dataFile : dirFile.listFiles()) {
				if (dataFile.isDirectory()) continue;
				if (!dataFile.getName().endsWith(".pnml")) continue;
				
				for (File dirFile2 : logFile.listFiles()) {
					if (!dirFile2.isDirectory()) continue;
					for (File dataFile2 : dirFile2.listFiles()) {
						if (dataFile2.isDirectory()) continue;
						if (!dataFile2.getName().endsWith(".xes")) continue;
						if (!dataFile2.getName().replace(".xes", "").equals(dataFile.getName().replace(".pnml", ""))) continue;
						
						System.out.println(dataFile.getName());
						System.out.println(dataFile2.getName());
						
						String[] config = new String[4];
						config[0] = dataFile2.getAbsolutePath();
						config[1] = dataFile.getAbsolutePath();
						config[2] = "experiments.BehavioralMetricExperiment";
						
						configurations.add(config);
						
					}
				}

			}
		}
		
		CSVExperimentRunner runner = new CSVExperimentRunner(experimentResultPath);
		runner.runExperiments(configurations);
	}
}
