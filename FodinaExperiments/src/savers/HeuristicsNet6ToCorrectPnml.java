package savers;

import java.io.File;
import java.io.IOException;

import net.seppe.prom.FakePluginContext;

import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.convertheuristicsnet.HeuristicsNetConverterPlugin;
import org.processmining.plugins.kutoolbox.utils.ExportUtils;
import org.processmining.plugins.kutoolbox.utils.PetrinetUtils;

public class HeuristicsNet6ToCorrectPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		try {
			Object[] result = HeuristicsNetConverterPlugin.toReducedPetrinet(new FakePluginContext(), net);
			Petrinet pnet = (Petrinet) result[0];
			ExportUtils.exportPetriNet(pnet, PetrinetUtils.getInitialMarking(pnet), pnmlFile);
		} catch (ConnectionCannotBeObtained | IOException e1) {
			e1.printStackTrace();
		}
	}
}
