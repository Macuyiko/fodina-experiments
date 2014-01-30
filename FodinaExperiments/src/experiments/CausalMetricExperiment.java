package experiments;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XLog;


import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.fitness.AbstractFitness;
import org.processmining.plugins.bpmnminer.fitness.FlowFitness;
import org.processmining.plugins.bpmnminer.fitness.FuzzyFitness;
import org.processmining.plugins.bpmnminer.fitness.ICSFitness;
import org.processmining.plugins.bpmnminer.fitness.PMFitness;
import org.processmining.plugins.bpmnminer.fitness.RecallFitness;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import utils.ResultsTable;

public class CausalMetricExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
			
		CausalNet net = (CausalNet) model;
		MinerSettings settings = new MinerSettings();
		settings.useUniqueStartEndTasks = false;
		EventLogTaskMapper mapper = new EventLogTaskMapper(log, settings.classifier);
		mapper.setup(settings.backwardContextSize, 
				settings.forwardContextSize, 
				settings.useUniqueStartEndTasks, 
				settings.collapseL1l,
				settings.taskThreshold,
				settings.duplicateThreshold);
		
		EventLogTaskMapper fitnessMapper = EventLogTaskMapper.createMapping(net, 
				XEventClasses.deriveEventClasses(settings.classifier, log),
				settings.classifier);
		
		System.out.println(fitnessMapper);
		
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
		
		results.put("ics", fitness1.getFitness());
		results.put("recall", fitness2.getFitness());
		results.put("pm", fitness3.getFitness());
		results.put("fuzzy", fitness4.getFitness());
		results.put("flow", fitness5.getFitness());
	}


}
