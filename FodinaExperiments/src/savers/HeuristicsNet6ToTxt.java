package savers;

import java.io.File;
import java.io.IOException;

import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.importexporthnet.ExportHNet;

public class HeuristicsNet6ToTxt extends AbstractSaver {
	public void save(File txtFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		ExportHNet exporter = new ExportHNet();
		try {
			exporter.exportHeuristicsNetToHNETFile(null, net, txtFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
