package experiments;

import org.deckfour.xes.model.XLog;


import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.kutoolbox.logmappers.PetrinetLogMapper;
import org.processmining.plugins.neconformance.PetrinetEvaluatorPlugin;

import utils.ResultsTable;

public class BehavioralMetricExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
			
		Petrinet net = (Petrinet) model;
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
