package experiments;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.mining.petrinetmining.PetriNetResult;
import org.processmining.mining.petrinetmining.TsinghuaAlphaProcessMiner;

import savers.Petrinet5ToPnml;
import be.kuleuven.econ.cbf.utils.log.LogReaderFacade;
import utils.ResultsTable;

public class AlphaMinerTsinghua5Experiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration,
			ResultsTable results) {
		
		// Put skip tests here (for logs which crash miner)

		LogReaderFacade logReader = new LogReaderFacade(log);
		
		results.tick("mining_time");
		TsinghuaAlphaProcessMiner miner = new TsinghuaAlphaProcessMiner();
		miner.getOptionsPanel(logReader.getLogSummary());
		PetriNetResult result = (PetriNetResult) miner.mine(logReader);
		results.tock("mining_time");
		
		PetriNet net = result.getPetriNet();
		
		// Save to petri
		Petrinet5ToPnml saver2 = new Petrinet5ToPnml();
		saver2.setSaveObject(net);
		String savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".pnml";
		System.out.println("Saving to: "+savePath);
		saver2.save(new File(savePath));		
	}

}
