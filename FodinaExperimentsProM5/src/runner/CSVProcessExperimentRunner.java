package runner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import utils.ResultsTable;

public class CSVProcessExperimentRunner extends CSVExperimentRunner {

	public String csvPath = null;
	
	public CSVProcessExperimentRunner(String csvPath) {
		super(csvPath);
	}
	
	@Override
	public ResultsTable runExperiment(String[] configuration) {
		ResultsTable result = new ResultsTable();
		
		Process p;
		try {
			p = createProcess(configuration);
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
		
		try {
			final ObjectInputStream ois = new ObjectInputStream(p.getInputStream());
			String l;
			while ((l = ois.readUTF()) != null) {
				Double d = ois.readDouble();
				result.put(l, d);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private Process createProcess(String[] configuration) throws IOException {
		// program command:
		// [java.home]/bin/java -cp [java.class.path] Main
		for (int s = 0; s < configuration.length; s++)
			if (configuration[s] == null)
				configuration[s] = "NULL";
		String separator = System.getProperty("file.separator");
		String executable =
		System.getProperty("java.home")
				+ separator + "bin"
				+ separator + "java";
		String classpath = System.getProperty("java.class.path");
		String main = ExperimentProcess.class.getCanonicalName();
		String[] command = new String[] { executable, "-Xmx4G", "-cp", classpath, main };
		String[] fullCommand = concat(command, configuration);
		return Runtime.getRuntime().exec(fullCommand);
	}
	
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
		
}
