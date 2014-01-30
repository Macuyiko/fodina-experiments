package loaders;

import java.io.File;
import net.seppe.prom.FakePluginContext;

import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.plugins.importexporthnet.ImportHNet;

public class HeuristicsNet6Loader extends AbstractLoader<HeuristicsNet> {

	@Override
	public HeuristicsNet load(File file) {
		ImportHNet importer = new ImportHNet();
		
		HeuristicsNet net;
		try {
			net = (HeuristicsNet) importer.importFile(new FakePluginContext(), file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return net;
	}


}
