package mains;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

import runner.CSVExperimentRunner;

public class AlphaMiners5Main {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\";
	public static final String outputPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String outputPathAlpha = outputPath + "alpha\\";
	public static final String outputPathAlphaT = outputPath + "alphat\\";
	public static final String outputPathAlphaPP = outputPath + "alphapp\\";
	public static final String experimentResultPath = outputPath+"alphaminers.csv";
	
	public static void main(String[] args) {
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(150000000);
		
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
				config[2] = "experiments.AlphaMiner5Experiment";
				config[3] = outputPathAlpha;
				configurations.add(config);
				
				config[0] = dataFile.getAbsolutePath();
				config[1] = null;
				config[2] = "experiments.AlphaMinerTsinghua5Experiment";
				config[3] = outputPathAlphaT;
				configurations.add(config);
				
				config[0] = dataFile.getAbsolutePath();
				config[1] = null;
				config[2] = "experiments.AlphaMinerPlusPlus5Experiment";
				config[3] = outputPathAlphaPP;
				configurations.add(config);
			
			}
		}
		
		CSVExperimentRunner runner = new CSVExperimentRunner(experimentResultPath);
		runner.runExperiments(configurations);
	}
}
