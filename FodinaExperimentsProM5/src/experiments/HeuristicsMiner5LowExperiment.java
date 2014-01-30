package experiments;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.mining.geneticmining.fitness.duplicates.DTImprovedContinuousSemanticsFitness;
import org.processmining.mining.heuristicsmining.HeuristicsMiner;
import org.processmining.mining.heuristicsmining.HeuristicsMinerGUI;
import org.processmining.mining.heuristicsmining.HeuristicsMinerParameters;
import org.processmining.mining.heuristicsmining.HeuristicsNetResult;

import savers.HeuristicsNet5ToPnml;
import be.kuleuven.econ.cbf.utils.log.LogReaderFacade;
import savers.HeuristicsNet5ToTxt;
import utils.ResultsTable;

public class HeuristicsMiner5LowExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
		
		// Put skip tests here (for logs which crash miner)
		
		LogReaderFacade logReader = new LogReaderFacade(log);
		
		results.tick("mining_time");
		HeuristicsMiner miner = new HeuristicsMiner();
		HeuristicsMinerParameters parameters = new HeuristicsMinerParameters();
		parameters.setDependencyThreshold(.09);
		parameters.setL1lThreshold(.09);
		parameters.setL2lThreshold(.09);
		parameters.setPositiveObservationsThreshold(1);
		HeuristicsMinerGUI panel = (HeuristicsMinerGUI) miner.getOptionsPanel(logReader.getLogSummary());
		panel.setHeuristicsMinerParameters(parameters);
		HeuristicsNetResult result = (HeuristicsNetResult) miner.mine(logReader);
		results.tock("mining_time");
		
		HeuristicsNet[] array = new HeuristicsNet[] { result.getHeuriticsNet() };
		results.put("ics5_beforedisconnect", array[0].getFitness());
		
		results.tick("fitness_time");
		DTImprovedContinuousSemanticsFitness fitness = new DTImprovedContinuousSemanticsFitness(logReader);
		fitness.calculate(array);
		results.tock("fitness_time");
		results.put("ics5_afterdisconnect", array[0].getFitness());
		
		// Save to txt and petri
		HeuristicsNet5ToTxt saver1 = new HeuristicsNet5ToTxt();
		saver1.setSaveObject(array[0]);
		String savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".txt";
		System.out.println("Saving to: "+savePath);
		saver1.save(new File(savePath));
		
		// Files to skip
		if(configuration[0].contains("realhospital.xes")) return;
		
		HeuristicsNet5ToPnml saver2 = new HeuristicsNet5ToPnml();
		saver2.setSaveObject(array[0]);
		savePath = configuration[3] + new File(configuration[0]).getName().replace(".xes", "") + ".pnml";
		System.out.println("Saving to: "+savePath);
		saver2.save(new File(savePath));
		
		
		
	}

}
