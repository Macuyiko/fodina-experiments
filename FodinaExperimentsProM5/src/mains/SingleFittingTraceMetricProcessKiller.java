package mains;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SingleFittingTraceMetricProcessKiller {
	public static void main(String[] arg) {
		// This little guy keeps an eye out for wacko-processes (might happen in some cases due to
		// IO lockups).
		
		Map<String, Integer> lastSeen = new HashMap<String,Integer>();
		while (true) {
			try {
				String line;
				String z = null;
				Process p = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq java.exe\" /V");
				BufferedReader input =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
					z = line;
					while (z.contains("  "))
						z = z.replace("  ", " ");
					String[] s = z.split(" ");
					if (s.length < 2) continue;
					if (s[1].equals("Name")) continue;
					if (s[1].equals("========")) continue;
					System.out.println(z);
					if (!lastSeen.containsKey(s[1]))
						lastSeen.put(s[1], 0);
					lastSeen.put(s[1], lastSeen.get(s[1])+1);
					if (lastSeen.get(s[1]) >= 60) {
						Runtime.getRuntime().exec("taskkill /PID " + s[1].trim()+" /T /F");
						System.out.println("KILLED");
						lastSeen.remove(s[1]);
					}
					System.out.println(s[1] + "  " + lastSeen.get(s[1]));
					
				}
				
				input.close();
				Thread.sleep(1000);
			} catch (Exception err) {
				err.printStackTrace();
			}

		}

	}
}
