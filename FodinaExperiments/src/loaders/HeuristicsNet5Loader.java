package loaders;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;

import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HNSubSet;
import org.processmining.models.heuristics.impl.HeuristicsNetImpl;
import org.processmining.plugins.importexporthnet.OrderedActivitiesMappingStructures;
import org.processmining.plugins.kutoolbox.utils.LogUtils;

public class HeuristicsNet5Loader extends AbstractLoader<HeuristicsNet> {

	private Map<Integer, String> readEventClasses;
	private HNSubSet start;
	private HNSubSet end;
	private Map<Integer, HNSet> inConnections;
	private Map<Integer, HNSet> outConnections;
	
	@Override
	public HeuristicsNet load(File file) {
		readEventClasses = new HashMap<Integer, String>();
		start = new HNSubSet();
		end = new HNSubSet();
		inConnections = new HashMap<Integer, HNSet>();
		outConnections = new HashMap<Integer, HNSet>();

		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			readIndividual(bis);
			bis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		
		XEventClasses xec = new XEventClasses(XLogInfoImpl.STANDARD_CLASSIFIER);
		for (int i = 0; i < readEventClasses.size(); i++) {
			String ec = readEventClasses.get(i);
			xec.register(LogUtils.deriveEventFromClassIdentity(ec, xec.getClassifier(), ":"));
		}
		ActivitiesMappingStructures ams = new OrderedActivitiesMappingStructures(xec);
		
		HeuristicsNet net = new HeuristicsNetImpl(ams);
		
		net.setStartActivities(start);
		net.setEndActivities(end);	
		net.setFitness(0);
		
		for (int e = 0; e < readEventClasses.size(); e++) {
			net.setInputSet(e, new HNSet());
			net.setOutputSet(e, new HNSet());
		}
		
		for (Entry<Integer, HNSet> e : inConnections.entrySet()) {
			net.setInputSet(e.getKey(), e.getValue());
		}

		for (Entry<Integer, HNSet> e : outConnections.entrySet()) {
			net.setOutputSet(e.getKey(), e.getValue());
		}
		
		return net;
	}


	private void readIndividual(InputStream paramInputStream) throws IOException {
		BufferedReader localBufferedReader = null;
		String str = null;
		localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
		
		StringTokenizer localST;
		
		while ((str = localBufferedReader.readLine()) != null)
			if ((str.trim().length() > 0) && (!str.equals("/////////////////////")))
			{
				localST = new StringTokenizer(str, "@");
				while (localST.hasMoreElements())
					start.add(Integer.parseInt(localST.nextToken()));
				break;
			}
		
		while ((str = localBufferedReader.readLine()) != null)
			if ((str.trim().length() > 0) && (!str.equals("/////////////////////")))
			{
				localST = new StringTokenizer(str, "@");
				while (localST.hasMoreElements())
					end.add(Integer.parseInt(localST.nextToken()));
				break;
			}
		
		while ((str = localBufferedReader.readLine()) != null)
			if ((str.trim().length() > 0) && (!str.equals("/////////////////////")))
			{
				Vector<String> localObject2 = new Vector<String>();
				do
				{
					if (str.trim().length() > 0)
						localObject2.add(str);
					str = localBufferedReader.readLine();
				} while ((str != null) && (!str.trim().equals("/////////////////////")));
				constructEventClasses(localObject2);
				localObject2 = null;
				break;
			}
		
		HNSet[] inputSets = new HNSet[readEventClasses.size()];
		HNSet[] outputSets = new HNSet[readEventClasses.size()];
		do {
			if ((str.trim().length() > 0) && (!str.trim().equals("/////////////////////")))
				loadSets(str, inputSets, outputSets);
			str = localBufferedReader.readLine();
		} while (str != null);
		
		for (int i = 0; i < inputSets.length; i++)
			inConnections.put(i, inputSets[i]);
		for (int i = 0; i < outputSets.length; i++)
			outConnections.put(i, outputSets[i]);
	}

	private void constructEventClasses(Vector<String> paramVector) {
		for (int i = 0; i < paramVector.size(); i++) {
			String str1 = (String) paramVector.get(i);
			String str2 = str1.substring(0, str1.indexOf("@"));
			String str3 = str1.substring(str1.indexOf("@") + "@".length(), str1.length() - 1);
			readEventClasses.put(Integer.parseInt(str3), str2);
		}
	}


	private void loadSets(String paramString, HNSet[] inputSets, HNSet[] outputSets) {
		int i = -1; int j = 0; int k = 0; j = 0;
		
		k = paramString.indexOf("@");
		String act = paramString.substring(j, k);
		i = Integer.parseInt(act);
		j = k + "@".length();
		
		k = paramString.indexOf("@", j);
		String first = paramString.substring(j, k);
		HNSet localHNSet1 = extractHNSet(first);
		inputSets[i] = localHNSet1;
		j = k + "@".length();
		
		String second = paramString.substring(j);
		HNSet localHNSet2 = extractHNSet(second);
		outputSets[i] = localHNSet2;
	}


	private HNSet extractHNSet(String paramString) {
		HNSet localHNSet = new HNSet();
		paramString = paramString.trim();
		if (!paramString.equals(".")) {
			StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, "&");
			while (localStringTokenizer1.hasMoreTokens()) {
				StringTokenizer localStringTokenizer2 = new StringTokenizer(localStringTokenizer1.nextToken().trim(), "|");
				HNSubSet localHNSubSet = new HNSubSet();
				while (localStringTokenizer2.hasMoreTokens())
					localHNSubSet.add(Integer.parseInt(localStringTokenizer2.nextToken()));
				localHNSet.add(localHNSubSet);
			}
		}
		return localHNSet;
	}


}
