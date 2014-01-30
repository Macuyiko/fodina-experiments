package savers;

import java.io.File;
import java.io.IOException;

import net.seppe.prom.FakePluginContext;

import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.flex.converter.PNFromFlex;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToFlexConverter;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class HeuristicsNet6ToFlexPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		Object[] result = HeuristicsNetToFlexConverter.converter(new FakePluginContext(), net);
		
		PNFromFlex converter = new PNFromFlex();
		Object[] result2 = converter.convertToPN(new FakePluginContext(), (Flex) result[0]);
		Petrinet pnet = (Petrinet) result2[0];
		try {
			ExportUtils.exportPetriNet(pnet, PetrinetUtils.getInitialMarking(pnet), pnmlFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
