package runner;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map.Entry;

import org.deckfour.xes.model.XLog;

import utils.ResultsTable;
import experiments.AbstractLogModelExperiment;

public class ExperimentProcess extends AbstractExperimentRunner {

	public static void main(String[] configuration) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(System.out);
			System.setOut(new PrintStream(new NullOutputStream()));
			System.in.close();
		} catch (Exception e) {
			// Folks, we're having some technical difficulties...
			e.printStackTrace();
			System.exit(1);
		}
		
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
		}
		
		try {
			for (Entry<String, Double> entry : result.entrySet()) {
				oos.writeUTF(entry.getKey());
				oos.writeDouble(entry.getValue());
				oos.flush();
			}
			// Clean up
			oos.flush();
			System.err.write('\0');
			System.err.flush();
			System.exit(0);
		} catch (Exception e) {
			// Folks, we're having some technical difficulties...
			e.printStackTrace();
			System.exit(1);
		}
	}
}

class NullOutputStream extends OutputStream {
	@Override
	public void write(int arg0) throws IOException {
	}
}
