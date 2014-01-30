package mains;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

import runner.CSVExperimentRunner;

public class SingleFittingTraceMetricMain {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\";
	public static final String outputPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String experimentResultPath = outputPath+"singletrace-rep.csv";
	
	public static void main(String[] args) {
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(500000000);
		
		File baseFile = new File(basePath);
		List<String[]> configurations = new ArrayList<String[]>();
		
		for (File dirFile : baseFile.listFiles()) {
			if (!dirFile.isDirectory()) continue;
			for (File dataFile : dirFile.listFiles()) {
				if (dataFile.isDirectory()) continue;
				if (!dataFile.getName().endsWith(".xes")) continue;
				
			
				System.out.println(dataFile.getName());
				
				String[] config = new String[4];
				config[0] = dataFile.getAbsolutePath();
				config[1] = null;
				config[2] = "experiments.SingleFittingTraceMetricExperiment";
				config[3] = outputPath;
				
				configurations.add(config);
			}
		}
		
		CSVExperimentRunner runner = new CSVExperimentRunner(experimentResultPath);
		runner.runExperiments(configurations);
	}
}
