package experiments;
import org.deckfour.xes.model.XLog;

import utils.ResultsTable;


public abstract class AbstractLogModelExperiment {
	
	public ResultsTable runExperiment(XLog log, Object model, String[] configuration) {
		ResultsTable results = new ResultsTable();
		
		results.tick("___experiment_runtime");
		runExperiment(log, model, configuration, results);
		results.tock("___experiment_runtime");
		
		return results;
	}

	public abstract void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results);
}
