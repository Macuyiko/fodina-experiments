package experiments;

import java.io.File;

import net.seppe.prom.FakePluginContext;

import org.deckfour.xes.model.XLog;


import org.processmining.models.flexiblemodel.Flex;
import org.processmining.plugins.causalnet.miner.FlexibleHeuristicsMinerPlugin;
import org.processmining.plugins.causalnet.miner.settings.HeuristicsMinerSettings;

import savers.FlexToCausal;
import savers.FlexToCausalPnml;
import savers.FlexToPnml;
import utils.ResultsTable;

public class FlexibleHeuristicsMiner6LowExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration,
			ResultsTable results) {
		
		results.tick("mining_time");
		HeuristicsMinerSettings settings = new HeuristicsMinerSettings();
		settings.setDependencyThreshold(0.09);
		settings.setL1lThreshold(0.09);
		settings.setL2lThreshold(0.09);
		Object[] result = FlexibleHeuristicsMinerPlugin.runFlexibleHeuristicsMiner(new FakePluginContext(), log, 
				settings);
		results.tock("mining_time");
		
		Flex flex = (Flex) result[0];
		
		//Save		
		FlexToCausal saver3 = new FlexToCausal();
		saver3.setSaveObject(flex);
		String savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".cnet";
		System.out.println("Saving to: "+savePath);
		saver3.save(new File(savePath));
		
		FlexToCausalPnml saver4 = new FlexToCausalPnml();
		saver4.setSaveObject(flex);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".cnet.pnml";
		System.out.println("Saving to: "+savePath);
		saver4.save(new File(savePath));
		
		FlexToPnml saver6 = new FlexToPnml();
		saver6.setSaveObject(flex);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".pnml";
		System.out.println("Saving to: "+savePath);
		saver6.save(new File(savePath));
		
	}

}
