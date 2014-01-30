package runner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import au.com.bytecode.opencsv.CSVWriter;
import utils.ResultsTable;

public class CSVExperimentRunner extends AbstractExperimentRunner {

	public String csvPath = null;
	
	public CSVExperimentRunner(String csvPath) {
		this.csvPath = csvPath;
	}
	
	@Override
	protected void handleResult(List<String[]> configurations, List<ResultsTable> results) {
		super.handleResult(configurations, results);
		
		try {
			FileWriter fileWriter = new FileWriter(csvPath);
			CSVWriter writer = new CSVWriter(fileWriter);
			
			List<String> columns = new ArrayList<String>();
			for (ResultsTable result : results)
				for (Entry<String, Double> entry : result.entrySet())
					if (!columns.contains(entry.getKey()))
						columns.add(entry.getKey());
			
			String[] line = new String[configurations.get(0).length + columns.size()];
			for (int i = 0; i < configurations.get(0).length; i++)
				line[i] = "configuration_"+i;
			for (int i = 0; i < columns.size(); i++)
				line[configurations.get(0).length + i] = columns.get(i);
			
			writer.writeNext(line);
			
			for (int h = 0; h < configurations.size(); h++) {
				String[] configuration = configurations.get(h);
				ResultsTable result = results.get(h);
				
				for (int i = 0; i < configuration.length; i++)
					line[i] = configuration[i];
				for (int i = 0; i < columns.size(); i++)
					line[configuration.length + i] = result.containsKey(columns.get(i)) 
						? result.get(columns.get(i))+""
						: "NA";
				
				writer.writeNext(line);
			}
			
			writer.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
}
