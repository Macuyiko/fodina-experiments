package mains;

import java.io.File;
import org.deckfour.xes.nikefs2.NikeFS2VirtualFileSystem;

import be.kuleuven.econ.cbf.CBFUIController;
import be.kuleuven.econ.cbf.input.InputSet;
import be.kuleuven.econ.cbf.input.Mapping;
import be.kuleuven.econ.cbf.metrics.MetricSet;
import be.kuleuven.econ.cbf.ui.ResultFrame;

public class CobefraMain {
	public static final String logPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\";
	public static final String modelPath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\";
	public static final String experimentResultPath = logPath+"behavioralmetrics.csv";
	
	public static void main(String[] args) {
		NikeFS2VirtualFileSystem.instance().setSwapFileSize(200000000);
		InputSet inputSet = new InputSet();		// Set of mappings (log + petri net)
		MetricSet metricSet = new MetricSet();	// Set of metrics
		
		File modelFile = new File(modelPath);
		File logFile = new File(logPath);
		
		for (File dirFile : modelFile.listFiles()) {
			if (!dirFile.isDirectory()) continue;
	/**/	if (!(dirFile.getAbsolutePath().contains("fodina"))) continue;
			for (File dataFile : dirFile.listFiles()) {
				if (dataFile.isDirectory()) continue;
				if (!dataFile.getName().endsWith(".pnml")) continue;
				
				for (File dirFile2 : logFile.listFiles()) {
					if (!dirFile2.isDirectory()) continue;
					for (File dataFile2 : dirFile2.listFiles()) {
						if (dataFile2.isDirectory()) continue;
	/**/				if (dataFile2.getName().startsWith("log-")) continue;
						if (!dataFile2.getName().endsWith(".xes")) continue;
						if (!dataFile2.getName().replace(".xes", "").equals(dataFile.getName().replace(".pnml", ""))) continue;
						
						System.out.println(dataFile.getAbsolutePath());
						System.out.println(dataFile2.getAbsolutePath());
						
						Mapping m = new Mapping(dataFile2.getAbsolutePath(), dataFile.getAbsolutePath());
						m.assignUnmappedToInvisible();
						inputSet.add(m);
					}
				}

			}
		}
		
		go(inputSet, metricSet);
	}
	
	public static void go(InputSet inputSet, MetricSet metricSet) {
		CBFUIController myController = new CBFUIController();
		myController.preload();
		myController.setInputSet(inputSet);
		myController.setMetricSet(metricSet);
		
		new Thread(myController).start();
		
		try {
			myController.waitForCompletion();
		} catch (InterruptedException e) {
			myController.cancel();
			System.err.println(e.getStackTrace());
		}
		
		
		if (myController.hasResult()) {
			ResultFrame resultFrame = new ResultFrame(myController);
			new Thread(resultFrame).start();
			try {
				resultFrame.waitForCompletion();
			} catch (InterruptedException e) {
				resultFrame.cancel();
				System.err.println(e.getStackTrace());
			}
			if (resultFrame.isCancelled())
				System.exit(1);
		}
	}
}
