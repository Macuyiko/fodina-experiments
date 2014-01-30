package savers;

import java.io.File;
import java.io.IOException;

import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.importexport.ExportCNet;

public class CausalToCausal extends AbstractSaver {
	public void save(File cnetFile) {
		CausalNet net = (CausalNet) this.getSaveObject();
		
		ExportCNet exporter = new ExportCNet();
		
		try {
			exporter.exportCausalNetToCNETFile(null, net, cnetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
