package experiments;

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
import utils.ResultsTable;

public class FodinaMulticonfigExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
		for (int collapseL1l : new int[]{0,1})
		for (int preventL2lWithL1l : new int[]{0,1})
		for (int preferAndToL2l : new int[]{0,1})
		for (int useAllConnectedHeuristics : new int[]{0,1})
		for (int useLongDistanceDependency : new int[]{0,1})
		for (int useUniqueStartEndTasks : new int[]{0,1}) {
			MinerSettings settings = new MinerSettings();
			EventLogTaskMapper mapper = new EventLogTaskMapper(log, settings.classifier);
			settings.collapseL1l = collapseL1l == 1;
			settings.preventL2lWithL1l = preventL2lWithL1l == 1;
			settings.preferAndToL2l = preferAndToL2l == 1;
			settings.useAllConnectedHeuristics = useAllConnectedHeuristics == 1;
			settings.useLongDistanceDependency = useLongDistanceDependency == 1;
			settings.useUniqueStartEndTasks = useUniqueStartEndTasks == 1;
			
			mapper.setup(settings.backwardContextSize, 
					settings.forwardContextSize, 
					settings.useUniqueStartEndTasks, 
					settings.collapseL1l, 
					settings.taskThreshold,
					settings.duplicateThreshold);
			
			Object[] result = FodinaMinerPlugin.runMiner(null, log, settings);
			
			CausalNet net = (CausalNet) result[0];
			
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
			
			String prefix = "collapseL1l "+collapseL1l+" | "+
					"preventL2lWithL1l "+preventL2lWithL1l+" | "+
					"preferAndToL2l "+preferAndToL2l+" | "+
					"useAllConnectedHeuristics "+useAllConnectedHeuristics+" | "+
					"useLongDistanceDependency "+useLongDistanceDependency+" | "+
					"useUniqueStartEndTasks "+useUniqueStartEndTasks+" | ";
			
			
			results.put(prefix+"ics", fitness1.getFitness());
			results.put(prefix+"recall", fitness2.getFitness());
			results.put(prefix+"pm", fitness3.getFitness());
			results.put(prefix+"fuzzy", fitness4.getFitness());
			results.put(prefix+"flow", fitness5.getFitness());
		
		}
	}

}
