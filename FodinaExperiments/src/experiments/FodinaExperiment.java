package experiments;

import java.io.File;

import org.deckfour.xes.model.XLog;


import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.fitness.AbstractFitness;
import org.processmining.plugins.bpmnminer.fitness.FlowFitness;
import org.processmining.plugins.bpmnminer.fitness.FuzzyFitness;
import org.processmining.plugins.bpmnminer.fitness.ICSFitness;
import org.processmining.plugins.bpmnminer.fitness.PMFitness;
import org.processmining.plugins.bpmnminer.fitness.RecallFitness;
import org.processmining.plugins.bpmnminer.plugins.FodinaMinerPlugin;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import savers.CausalToCausal;
import savers.CausalToCausalPnml;
import utils.ResultsTable;

public class FodinaExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
			
		MinerSettings settings = new MinerSettings();
		settings.useUniqueStartEndTasks = false;
		EventLogTaskMapper mapper = new EventLogTaskMapper(log, settings.classifier);
		mapper.setup(settings.backwardContextSize, 
				settings.forwardContextSize, 
				settings.useUniqueStartEndTasks, 
				settings.collapseL1l, 
				settings.taskThreshold,
				settings.duplicateThreshold);
		
		results.tick("mining_time");
		Object[] result = FodinaMinerPlugin.runMiner(null, log, settings);
		results.tock("mining_time");
		
		CausalNet net = (CausalNet) result[0];
		
		results.tick("fitness_time");
		EventLogTaskMapper fitnessMapper = EventLogTaskMapper.createMapping(net, settings.classifier);
		AbstractFitness fitness1 = new ICSFitness(fitnessMapper, net);
		fitness1.replayLog(mapper.getGroupedLog());
		AbstractFitness fitness2 = new RecallFitness(fitnessMapper, net);
		fitness2.replayLog(mapper.getGroupedLog());
		AbstractFitness fitness3 = new PMFitness(fitnessMapper, net);
		fitness3.replayLog(mapper.getGroupedLog());
		AbstractFitness fitness4 = new FuzzyFitness(fitnessMapper, net);
		fitness4.replayLog(mapper.getGroupedLog());
		AbstractFitness fitness5 = new FlowFitness(fitnessMapper, net);
		fitness5.replayLog(mapper.getGroupedLog());
		results.tock("fitness_time");
		
		results.put("ics", fitness1.getFitness());
		results.put("recall", fitness2.getFitness());
		results.put("pm", fitness3.getFitness());
		results.put("fuzzy", fitness4.getFitness());
		results.put("flow", fitness5.getFitness());
		
		// Save
		CausalToCausal saver1 = new CausalToCausal();
		saver1.setSaveObject(net);
		String savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".cnet";
		System.out.println("Saving to: "+savePath);
		saver1.save(new File(savePath));
		
		CausalToCausalPnml saver2 = new CausalToCausalPnml();
		saver2.setSaveObject(net);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".pnml";
		System.out.println("Saving to: "+savePath);
		saver2.save(new File(savePath));	
	}

}
