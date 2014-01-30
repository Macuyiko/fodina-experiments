package savers;

import java.io.File;
import java.io.IOException;

import net.seppe.prom.FakePluginContext;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.FlexToCausalNet;
import org.processmining.plugins.bpmnminer.importexport.ExportCNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToFlexConverter;

public class HeuristicsNet6ToFlexCausal extends AbstractSaver {
	public void save(File cnetFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		Object[] result = HeuristicsNetToFlexConverter.converter(new FakePluginContext(), net);
		
		CausalNet cnet = FlexToCausalNet.convert(null, (Flex) result[0]);
		ExportCNet exporter = new ExportCNet();
		
		try {
			exporter.exportCausalNetToCNETFile(null, cnet, cnetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
