package experiments;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import utils.ResultsTable;

public class LogStatisticsExperiment extends AbstractLogModelExperiment {

	@Override
	public void runExperiment(XLog log, Object model, String[] configuration, ResultsTable results) {
		results.put("nr_traces", (double) getLogSize(log));
		results.put("nr_traces_distinct", (double) getDistinctLogSize(log));
		results.put("nr_activities", (double) getNrActivities(log));
		results.put("nr_starting_activities", (double) getNrStartActivities(log));
		results.put("nr_ending_activities", (double) getNrEndActivities(log));
		results.put("min_trace_length", (double) calcMin(getTraceSizes(log)));
		results.put("max_trace_length", (double) calcMax(getTraceSizes(log)));
		results.put("mean_trace_length", (double) calcMean(getTraceSizes(log)));
		results.put("sd_trace_length", (double) calcStanDev(getTraceSizes(log)));
		results.put("var_trace_length", (double) calcVariance(getTraceSizes(log)));
	}

	private static int getLogSize(XLog log) {
		return log.size();
	}
	
	private static int getDistinctLogSize(XLog log) {
		Set<String> distinct = new HashSet<String>();
		for (XTrace t : log)
			distinct.add(classifyTrace(t));
		return distinct.size();
	}
	
	private static String classifyTrace(XTrace trace) {
		String c = "";
		for (XEvent e : trace)
			c += XConceptExtension.instance().extractName(e) + "__<>__";
		return c;
	}
	
	private static int getNrActivities(XLog log) {
		Set<String> acts = new HashSet<String>();
		for (XTrace t : log)
			for (XEvent e : t)
				acts.add(XConceptExtension.instance().extractName(e));
		return acts.size();
	}
	
	private static int getNrStartActivities(XLog log) {
		Set<String> start = new HashSet<String>();
		for (XTrace t : log) {
			start.add(XConceptExtension.instance().extractName(t.get(0)));
		}
		return start.size();
	}
	
	private static int getNrEndActivities(XLog log) {
		Set<String> end = new HashSet<String>();
		for (XTrace t : log) {
			end.add(XConceptExtension.instance().extractName(t.get(t.size()-1)));
		}
		return end.size();
	}
	
	private static int[] getTraceSizes(XLog log) {
		int[] s = new int[log.size()];
		for (int t = 0; t < log.size(); t++)
			s[t] = log.get(t).size();
		return s;
	}
	
	private static double calcMin(int[] s) {
		double min = s[0];
		for (int v : s)
			min = Math.min(v, min);
		return (double) min;
	}
	
	private static double calcMax(int[] s) {
		double max = s[0];
		for (int v : s)
			max = Math.max(v, max);
		return (double) max;
	}
	
	private static double calcMean(int[] s) {
		double total = 0;
		for (int v : s)
			total += v;
		return (double) total / (double) s.length;
	}
	
	private static double calcStanDev(int[] s) {
		return Math.pow(calcVariance(s), 0.5);
	}

	private static double calcVariance(int[] s) {
		double total = 0;
		double sTotal = 0;
		double scalar = 1 / (double) (s.length - 1);
		for (int i = 0; i < s.length; i++) {
			total += s[i];
			sTotal += Math.pow(s[i], 2);
		}
		return (scalar * (sTotal - (Math.pow(total, 2) / s.length)));
	}

}
