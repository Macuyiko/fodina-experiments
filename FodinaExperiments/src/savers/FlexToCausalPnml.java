package savers;

import java.io.File;
import java.io.IOException;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.CausalNetToPetrinet;
import org.processmining.plugins.bpmnminer.converter.FlexToCausalNet;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class FlexToCausalPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		Flex net = (Flex) this.getSaveObject();
		
		CausalNet cnet = FlexToCausalNet.convert(null, net);
		Object[] result2 = CausalNetToPetrinet.convert(null, cnet);
		Petrinet pnet = (Petrinet) result2[0];
		
		try {
			ExportUtils.exportPetriNet(pnet, PetrinetUtils.getInitialMarking(pnet), pnmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
