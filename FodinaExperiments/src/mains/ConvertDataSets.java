package mains;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.ImportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class ConvertDataSets {
	public static final String basePath = "C:\\Users\\n11093\\Documents\\Papers Seppe\\Fodina (IS)\\experiment\\logs\\";
	
	public static void main(String[] args) throws IOException {
		File baseFile = new File(basePath);
		
		for (File dataFile : baseFile.listFiles()) {
			if (!dataFile.isDirectory()) {
				System.out.println(dataFile.getName());
				
				Petrinet net = null;
				XLog log = null;
				if (dataFile.getName().endsWith(".tpn")) {
					net = ImportUtils.openTPN(dataFile);
				} else if (dataFile.getName().endsWith(".pnml")) {
					// No conversion is done
				} else if (dataFile.getName().endsWith(".xes")) {
					// No conversion is done
				} else if (dataFile.getName().endsWith(".xes.gz")) {
					log = ImportUtils.openXESGZ(dataFile);
				} else if (dataFile.getName().endsWith(".mxml")) {
					log = ImportUtils.openMXML(dataFile);
				} else if (dataFile.getName().endsWith(".mxml.gz")) {
					log = ImportUtils.openMXMLGZ(dataFile);
				}
				
				if (net != null)
					ExportUtils.exportPetriNet(net, PetrinetUtils.getInitialMarking(net), 
							new File(dataFile.getAbsolutePath().replace(".tpn", ".pnml")));
				if (log != null)
					ExportUtils.exportLog(log,
							new File(dataFile.getAbsolutePath().replace(".gz", "").replace(".mxml", ".xes")));
			}
		}
	}
}
