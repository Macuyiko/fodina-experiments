package runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.kutoolbox.utils.ImportUtils;

import utils.ResultsTable;
import experiments.AbstractLogModelExperiment;

public abstract class AbstractExperimentRunner {
	
	public void runExperiments(List<String[]> configurations) {
		List<ResultsTable> results = new ArrayList<ResultsTable>();
		for (String[] configuration : configurations) {
			System.out.println("*** NEW EXPERIMENT ***");
			for (int i = 0; i < configuration.length; i++)
				System.out.println(i+": "+configuration[i]);
			results.add(runExperiment(configuration));
		}
		handleResult(configurations, results);
	}

	public ResultsTable runExperiment(String[] configuration) {
		ResultsTable result = new ResultsTable();
		try {
			String logPath = configuration[0];
			String modelPath = configuration[1];
			XLog log = parseLog(logPath);
			Object model = parseModel(modelPath);
			Class<?> cls = Class.forName(configuration[2]);
			AbstractLogModelExperiment experiment = (AbstractLogModelExperiment) cls.newInstance();
			
			result = experiment.runExperiment(log, model, configuration);

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	protected void handleResult(List<String[]> configurations, List<ResultsTable> results) {
		assert(configurations.size() == results.size());
		assert(configurations.size() > 0);
	}

	protected XLog parseLog(String path) {
		if (path == null) return null;
		return ImportUtils.openLog(new File(path));
	}
	
	protected Object parseModel(String path) {
		if (path == null) return null;
		return ImportUtils.openPetrinet(new File(path));
	}
	
}
