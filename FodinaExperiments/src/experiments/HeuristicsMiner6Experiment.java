package experiments;

import java.io.File;

import net.seppe.prom.FakePluginContext;

import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;


import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.HeuristicsMiner;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness.ImprovedContinuousSemantics;

import savers.HeuristicsNet6ToCorrectPnml;
import savers.HeuristicsNet6ToFlexCausal;
import savers.HeuristicsNet6ToFlexCausalPnml;
import savers.HeuristicsNet6ToFlexPnml;
import savers.HeuristicsNet6ToTxt;
import savers.HeuristicsNet6ToWrongPnml;
import utils.ResultsTable;

public class HeuristicsMiner6Experiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration,
			ResultsTable results) {
			
		results.tick("mining_time");
		HeuristicsMiner miner = new HeuristicsMiner(new FakePluginContext(), log);
		HeuristicsNet result = miner.mine();
		results.tock("mining_time");
		results.put("ics6_beforedisconnect", result.getFitness());
		
		HeuristicsNet[] population = new HeuristicsNet[1];
		population[0] = result;
		
		results.tick("fitness_time");
		ImprovedContinuousSemantics fitness = new ImprovedContinuousSemantics(XLogInfoFactory.createLogInfo(log));
		fitness.calculate(population);
		results.tock("fitness_time");
		results.put("ics6_afterdisconnect", population[0].getFitness());
		
		// Save
		HeuristicsNet6ToTxt saver1 = new HeuristicsNet6ToTxt();
		saver1.setSaveObject(population[0]);
		String savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".hnet";
		System.out.println("Saving to: "+savePath);
		saver1.save(new File(savePath));
		
		HeuristicsNet6ToCorrectPnml saver2 = new HeuristicsNet6ToCorrectPnml();
		saver2.setSaveObject(population[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".correct.pnml";
		System.out.println("Saving to: "+savePath);
		saver2.save(new File(savePath));
				
		HeuristicsNet6ToWrongPnml saver6 = new HeuristicsNet6ToWrongPnml();
		saver6.setSaveObject(population[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".pnml";
		System.out.println("Saving to: "+savePath);
		saver6.save(new File(savePath));
		
		HeuristicsNet6ToFlexCausal saver3 = new HeuristicsNet6ToFlexCausal();
		saver3.setSaveObject(population[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".flex.cnet";
		System.out.println("Saving to: "+savePath);
		saver3.save(new File(savePath));
		
		HeuristicsNet6ToFlexCausalPnml saver4 = new HeuristicsNet6ToFlexCausalPnml();
		saver4.setSaveObject(population[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".flex.cnet.pnml";
		System.out.println("Saving to: "+savePath);
		saver4.save(new File(savePath));
		
		HeuristicsNet6ToFlexPnml saver5 = new HeuristicsNet6ToFlexPnml();
		saver5.setSaveObject(population[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".flex.pnml";
		System.out.println("Saving to: "+savePath);
		saver5.save(new File(savePath));
		
		
	}

}
