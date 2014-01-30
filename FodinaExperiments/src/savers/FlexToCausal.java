package savers;

import java.io.File;
import java.io.IOException;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.FlexToCausalNet;
import org.processmining.plugins.bpmnminer.importexport.ExportCNet;

public class FlexToCausal extends AbstractSaver {
	public void save(File cnetFile) {
		Flex net = (Flex) this.getSaveObject();
		
		CausalNet cnet = FlexToCausalNet.convert(null, net);
		ExportCNet exporter = new ExportCNet();
		
		try {
			exporter.exportCausalNetToCNETFile(null, cnet, cnetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
