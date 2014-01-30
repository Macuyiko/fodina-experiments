package mains;

import java.io.File;
import java.io.IOException;

import loaders.HeuristicsNet5Loader;
import org.processmining.models.heuristics.HeuristicsNet;
import savers.HeuristicsNet6ToFlexCausal;
import savers.HeuristicsNet6ToTxt;

public class ConvertHM5 {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\models\\hm5low\\";
	
	public static void main(String[] args) throws IOException {
		File baseFile = new File(basePath);
		HeuristicsNet5Loader loader = new HeuristicsNet5Loader();
		
		for (File dataFile : baseFile.listFiles()) {
			if (!dataFile.isDirectory()) {
				if (!dataFile.getName().endsWith(".txt")) continue;

				System.out.println(dataFile.getName());
				
				HeuristicsNet hnet = loader.load(dataFile);
				
				HeuristicsNet6ToTxt saver1 = new HeuristicsNet6ToTxt();
				saver1.setSaveObject(hnet);
				String savePath = basePath + dataFile.getName().replace(".txt", "") + ".hnet";
				System.out.println("Saving to: "+savePath);
				saver1.save(new File(savePath));
				
			//	HeuristicsNet6ToCorrectPnml saver2 = new HeuristicsNet6ToCorrectPnml();
			//	saver2.setSaveObject(hnet);
			//	savePath = basePath + dataFile.getName().replace(".txt", "") + ".correct.pnml";
			//	System.out.println("Saving to: "+savePath);
			//	saver2.save(new File(savePath));
				
			//	HeuristicsNet6ToWrongPnml saver6 = new HeuristicsNet6ToWrongPnml();
			//	saver6.setSaveObject(hnet);
			//	savePath = basePath + dataFile.getName().replace(".txt", "") + ".pnml";
			//	System.out.println("Saving to: "+savePath);
			//	saver6.save(new File(savePath));
				
				HeuristicsNet6ToFlexCausal saver3 = new HeuristicsNet6ToFlexCausal();
				saver3.setSaveObject(hnet);
				savePath = basePath + dataFile.getName().replace(".txt", "") + ".flex.cnet";
				System.out.println("Saving to: "+savePath);
				saver3.save(new File(savePath));
				
			//	HeuristicsNet6ToFlexCausalPnml saver4 = new HeuristicsNet6ToFlexCausalPnml();
			//	saver4.setSaveObject(hnet);
			//	savePath = basePath + dataFile.getName().replace(".txt", "") + ".flex.cnet.pnml";
			//	System.out.println("Saving to: "+savePath);
			//	saver4.save(new File(savePath));
				
			//	HeuristicsNet6ToFlexPnml saver5 = new HeuristicsNet6ToFlexPnml();
			//	saver5.setSaveObject(hnet);
			//	savePath = basePath + dataFile.getName().replace(".txt", "") + ".flex.pnml";
			//	System.out.println("Saving to: "+savePath);
			//	saver5.save(new File(savePath));
				
			}
		}
	}
}
