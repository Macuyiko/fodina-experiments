package savers;

import java.io.File;
import java.io.IOException;

import net.seppe.prom.FakePluginContext;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToPetriNetConverter;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class HeuristicsNet6ToWrongPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		Object[] result = HeuristicsNetToPetriNetConverter.converter(new FakePluginContext(), net);
		Petrinet pnet = (Petrinet) result[0];
		try {
			ExportUtils.exportPetriNet(pnet, PetrinetUtils.getInitialMarking(pnet), pnmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
