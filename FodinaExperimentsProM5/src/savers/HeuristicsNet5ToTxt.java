package savers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.processmining.framework.models.heuristics.HeuristicsNet;


public class HeuristicsNet5ToTxt extends AbstractSaver {
	public void save(File txtFile) {
		HeuristicsNet net = (HeuristicsNet) this.getSaveObject();
		try {
			FileOutputStream fos = new FileOutputStream(txtFile);
			net.toFile(fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
