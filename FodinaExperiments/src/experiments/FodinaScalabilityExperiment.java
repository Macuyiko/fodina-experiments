package experiments;

import net.seppe.prom.FakePluginContext;

import org.deckfour.xes.model.XLog;


import org.processmining.plugins.bpmnminer.plugins.FodinaMinerPlugin;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import org.processmining.plugins.causalnet.miner.FlexibleHeuristicsMinerPlugin;
import org.processmining.plugins.causalnet.miner.settings.HeuristicsMinerSettings;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;

import utils.ResultsTable;

public class FodinaScalabilityExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
			
		results.tick("mining_time");
		if (configuration[4].equals("fodina"))
			doFodina(log);
		if (configuration[4].equals("hm6"))
			doHM6(log);
		if (configuration[4].equals("fhm6"))
			doFHM6(log);
		results.tock("mining_time");
	}
	
	public void doFodina(XLog log) {
		MinerSettings settings = new MinerSettings();
		EventLogTaskMapper mapper = new EventLogTaskMapper(log, settings.classifier);
		settings.useUniqueStartEndTasks = false;
			
		mapper.setup(settings.backwardContextSize, 
					settings.forwardContextSize, 
					settings.useUniqueStartEndTasks, 
					settings.collapseL1l, 
					settings.taskThreshold,
					settings.duplicateThreshold);
		
		FodinaMinerPlugin.runMiner(null, log, settings);
		
	}

	public void doHM6(XLog log) {
		HeuristicsMiner miner = new HeuristicsMiner(new FakePluginContext(), log);
		miner.mine();
	}
	
	public void doFHM6(XLog log) {
		FlexibleHeuristicsMinerPlugin.runFlexibleHeuristicsMiner(new FakePluginContext(), log, 
				new HeuristicsMinerSettings());
	}
}
