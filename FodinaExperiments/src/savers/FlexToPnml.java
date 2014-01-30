package savers;

import java.io.File;
import java.io.IOException;

import net.seppe.prom.FakePluginContext;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.flex.converter.PNFromFlex;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class FlexToPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		Flex net = (Flex) this.getSaveObject();
		
		PNFromFlex converter = new PNFromFlex();
		Object[] result2 = converter.convertToPN(new FakePluginContext(), net);
		Petrinet pnet = (Petrinet) result2[0];
		try {
			ExportUtils.exportPetriNet(pnet, PetrinetUtils.getInitialMarking(pnet), pnmlFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
