package savers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.processmining.converting.HNetToPetriNetConverter;
import org.processmining.exporting.petrinet.PnmlExport;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.framework.ui.UISettings;
import org.processmining.mining.petrinetmining.PetriNetResult;

import savers.AbstractSaver;


public class HeuristicsNet5ToPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		UISettings.getInstance().setCustomDotLocation("dot.exe");
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		HNetToPetriNetConverter plugin = new HNetToPetriNetConverter();
		PetriNetResult pnet = (PetriNetResult) plugin.convert(new ProvidedObject(" ", new Object[]{net, null}));
		PnmlExport export = new PnmlExport();
		try {
			OutputStream fos = new FileOutputStream(pnmlFile);
			export.export(new ProvidedObject(" ", new Object[]{ pnet.getPetriNet() }), fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
