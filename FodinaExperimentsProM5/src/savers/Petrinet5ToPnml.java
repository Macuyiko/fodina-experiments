package savers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.algorithms.PnmlWriter;
import org.processmining.framework.plugin.ProvidedObject;
import org.processmining.framework.ui.UISettings;

import savers.AbstractSaver;

public class Petrinet5ToPnml extends AbstractSaver {
	public void save(File pnmlFile) {
		UISettings.getInstance().setCustomDotLocation("dot.exe");
		PetriNet net = (PetriNet) this.getSaveObject();
		try {
			OutputStream fos = new FileOutputStream(pnmlFile);
			ProvidedObject po = new ProvidedObject(" ", new Object[]{ net });
			Object[] o = po.getObjects();
			for (int i = 0; i < o.length; i++) {
				if (o[i] instanceof PetriNet) {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					PnmlWriter.write(false, true, (PetriNet) o[i], bw);
					bw.close();
					return;
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
