package utils;

import java.util.HashMap;

public class ResultsTable extends HashMap<String, Double> {
	private static final long serialVersionUID = -9134395748310038838L;

	@Override
	public Double put(String label, Double value) {
		System.out.println(System.currentTimeMillis()+": "+label+" --> "+value);
		return super.put(label, value);
	}
	
	public void tick(String label) {
		put(label, (System.currentTimeMillis() / 1000D));
	}
	
	public void tock(String label) {
		put(label, (System.currentTimeMillis() / 1000D) - get(label));
	}
	
	public void clac() {
		clear();
	}
}
