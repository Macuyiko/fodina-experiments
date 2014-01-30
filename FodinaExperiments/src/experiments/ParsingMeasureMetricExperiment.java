package experiments;

import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;


import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness.ProperCompletion;
import org.processmining.plugins.kutoolbox.logmappers.PetrinetLogMapper;
import org.processmining.plugins.neconformance.PetrinetEvaluatorPlugin;

import utils.ResultsTable;

public class ParsingMeasureMetricExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
		
		if (configuration[1].contains(".pnml")) {
			Petrinet net = (Petrinet) model;
			
			double good = 0;
			double total = log.size();
			for (XTrace t : log) {
				XLog newLog = new XLogImpl((XAttributeMap) log.getAttributes().clone());
				newLog.add((XTrace) t.clone());
				PetrinetLogMapper mapper = PetrinetLogMapper.getStandardMap(newLog, net);
				System.out.println(mapper);
				double recall = PetrinetEvaluatorPlugin.getMetricValue(
						newLog, net, mapper, 
						false, false, true, 
						false, false, 
						-1, -1, 
						true, true, true, 
						true, "recall");
				newLog = null;
				if (recall >= 1D) good = good + 1;
			}
			double result = good / total;
			results.put("parsing_measure_pnml", result);
		}
		
		if (configuration[1].contains(".hnet")) {
			HeuristicsNet net = (HeuristicsNet) model;
			HeuristicsNet[] population = new HeuristicsNet[1];
			population[0] = net;
			ProperCompletion completion = new ProperCompletion(XLogInfoFactory.createLogInfo(log));
			completion.calculate(population);
			results.put("parsing_measure_hnet", population[0].getFitness());
		}

	}

}
