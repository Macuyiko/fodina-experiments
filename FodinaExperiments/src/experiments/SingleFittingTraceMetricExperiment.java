package experiments;

import net.seppe.prom.FakePluginContext;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;


import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.models.flexiblemodel.Flex;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.converter.FlexToCausalNet;
import org.processmining.plugins.bpmnminer.fitness.AbstractFitness;
import org.processmining.plugins.bpmnminer.fitness.ICSFitness;
import org.processmining.plugins.bpmnminer.plugins.FodinaMinerPlugin;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import org.processmining.plugins.causalnet.miner.FlexibleHeuristicsMinerPlugin;
import org.processmining.plugins.causalnet.miner.settings.HeuristicsMinerSettings;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness.ImprovedContinuousSemantics;
import org.processmining.plugins.kutoolbox.groupedlog.GroupedXLog;

import utils.ResultsTable;

public class SingleFittingTraceMetricExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration,
			ResultsTable results) {
		
		double[] good = new double[3];
		GroupedXLog gLog = new GroupedXLog(log);
		
		for (int t = 0; t < gLog.size(); t++) {
			XLog newLog = new XLogImpl((XAttributeMap) log.getAttributes().clone());
			newLog.add((XTrace) gLog.get(t).get(0).clone());
			double s = (double) gLog.get(t).size();
			good[0] += s * calculateFlexibleHeuristicsMiner6Low(newLog, configuration);
			good[1] += s * calculateFodinaLow(newLog, configuration);
			good[2] += s * calculateHeuristicsMiner6Low(newLog, configuration);
			newLog = null;
		}
	
		results.put("fhm6low", good[0] / (double)log.size());
		results.put("fodinalow", good[1] / (double)log.size());
		results.put("hm6low", good[2] / (double)log.size());

	}

	private double calculateHeuristicsMiner6Low(XLog newLog, String[] configuration) {
		
		org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings settings = 
				new org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings();
		settings.setDependencyThreshold(0.0);
		settings.setL1lThreshold(0.0);
		settings.setL2lThreshold(0.0);
		org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner miner = 
				new org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner(new FakePluginContext(), newLog, settings);
		HeuristicsNet result = miner.mine();
		HeuristicsNet[] population = new HeuristicsNet[1];
		population[0] = result;
		ImprovedContinuousSemantics fitness = new ImprovedContinuousSemantics(XLogInfoFactory.createLogInfo(newLog));
		fitness.calculate(population);
		if (population[0].getFitness() >= 1D) return 1D;
		return 0D;
	}

	private double calculateFodinaLow(XLog newLog, String[] configuration) {
		MinerSettings settings = new MinerSettings();
		settings.dependencyThreshold = 0.0;
		settings.l1lThreshold = 0.0;
		settings.l2lThreshold = 0.0;
		settings.useUniqueStartEndTasks = true;
		settings.useAllConnectedHeuristics = true;
		settings.patternThreshold = 0;
		EventLogTaskMapper mapper = new EventLogTaskMapper(newLog, settings.classifier);
		mapper.setup(settings.backwardContextSize, 
				settings.forwardContextSize, 
				settings.useUniqueStartEndTasks, 
				settings.collapseL1l, 
				settings.taskThreshold,
				settings.duplicateThreshold);
		
		Object[] result = FodinaMinerPlugin.runMiner(null, newLog, settings);
		CausalNet net = (CausalNet) result[0];
		AbstractFitness fitness1 = new ICSFitness(mapper, net);
		fitness1.replayLog(mapper.getGroupedLog());
		System.err.println(fitness1.getFitness());
		if (fitness1.getFitness() >= 1D) return 1D;
		for (int i = 0; i < newLog.get(0).size(); i++)
			System.err.print(XConceptExtension.instance().extractName(newLog.get(0).get(i)));
		System.err.println();
		return 0D;
	}

	private double calculateFlexibleHeuristicsMiner6Low(XLog newLog, String[] configuration) {
		MinerSettings csettings = new MinerSettings();
		HeuristicsMinerSettings settings = new HeuristicsMinerSettings();
		settings.setDependencyThreshold(0.0);
		settings.setL1lThreshold(0.0);
		settings.setL2lThreshold(0.0);
		Object[] result = FlexibleHeuristicsMinerPlugin.runFlexibleHeuristicsMiner(new FakePluginContext(), newLog, settings);
		Flex flex = (Flex) result[0];
		CausalNet cnet = FlexToCausalNet.convert(null, flex);
		EventLogTaskMapper fitnessMapper = EventLogTaskMapper.createMapping(cnet, newLog, csettings.classifier);
		AbstractFitness fitness1 = new ICSFitness(fitnessMapper, cnet);
		fitness1.replayLog(fitnessMapper.getGroupedLog());
		if (fitness1.getFitness() >= 1D) return 1D;
		return 0D;
	}

}
