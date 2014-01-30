package experiments;

import org.deckfour.xes.model.XLog;


import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.CausalNetToPetrinet;
import org.processmining.plugins.bpmnminer.plugins.FodinaMinerPlugin;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import org.processmining.plugins.kutoolbox.logmappers.PetrinetLogMapper;
import org.processmining.plugins.neconformance.PetrinetEvaluatorPlugin;

import utils.ResultsTable;

public class FodinaPrecGenExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
		MinerSettings settings = new MinerSettings();
		settings.useUniqueStartEndTasks = false;
		settings.patternThreshold = Double.parseDouble(configuration[6]);
			
		results.tick("mining_time");
		Object[] result = FodinaMinerPlugin.runMiner(null, log, settings);
		results.tock("mining_time");
		
		CausalNet cnet = (CausalNet) result[0];
		Object[] result2 = CausalNetToPetrinet.convert(null, cnet);
		Petrinet net = (Petrinet) result2[0];
		PetrinetLogMapper mapper = PetrinetLogMapper.getStandardMap(log, net);
		System.out.println(mapper.toString());
		
		double recall = PetrinetEvaluatorPlugin.getMetricValue(
				log, net, mapper, 
				false, false, true, 
				false, false, 
				-1, -1, 
				true, true, true, 
				true, "recall");
		double precision = PetrinetEvaluatorPlugin.getMetricValue(
				log, net, mapper, 
				false, false, true, 
				false, false, 
				-1, -1, 
				true, true, true, 
				true, "precision");
		double generalization = PetrinetEvaluatorPlugin.getMetricValue(
				log, net, mapper, 
				false, false, true, 
				false, false, 
				-1, -1, 
				true, true, true, 
				true, "generalization");
		
		results.put("behavioral_recall", recall);
		results.put("behavioral_precision", precision);
		results.put("behavioral_generalization", generalization);
	}

}
