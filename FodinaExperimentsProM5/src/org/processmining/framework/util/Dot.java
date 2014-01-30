package org.processmining.framework.util;

import java.io.File;

import org.processmining.framework.ui.UISettings;

import att.grappa.Graph;
import att.grappa.Parser;

public class Dot {

	public static String getDotPath() {
		String customDot = UISettings.getInstance().getCustomDotLocation();
		if (customDot != null) {
			return customDot;
		} else if (RuntimeUtils.isRunningWindows()) {
			return "dot" + System.getProperty("file.separator") + "dot.exe";
		} else if (RuntimeUtils.isRunningMacOsX()) {
			return "/Applications/Graphviz.app/Contents/MacOS/dot";
		} else {
			return "dot";
		}
	}

	private Dot() {
	}

	public static Graph execute(String dotFilename, boolean deleteFileAfterwards) throws Exception {
		return execute(dotFilename);
	}

	public static Graph execute(String dotFilename) throws Exception {

		DOTDialog dialog = new DOTDialog(dotFilename);
		Graph ret = dialog.runDot();
		return ret;
	}
}

class DOTDialog {

	private Process dot;
	private String dotFilename;

	public DOTDialog(String dotFilename) throws Exception {
		System.out.println("Running dot...");
		this.dotFilename = dotFilename;
	}


	public Graph runDot() throws Exception {
		File dotFile = new File(dotFilename);
		Parser parser;
		Graph graph;

		String customDot = UISettings.getInstance().getCustomDotLocation();
		if (customDot != null) {
			String dotCommandString = customDot + " -q 5 ";
			if (RuntimeUtils.isRunningWindows()) {
				dotCommandString += "\"" + dotFile.getAbsolutePath() + "\"";
			} else {
				dotCommandString += dotFile.getAbsolutePath();
			}
			dot = Runtime.getRuntime().exec(dotCommandString);
		} else if (RuntimeUtils.isRunningWindows()) {
			dot = Runtime.getRuntime().exec(
					"dot" + System.getProperty("file.separator")
							+ "dot.exe -q5 \"" + dotFile.getAbsolutePath()
							+ "\"");
		} else if (RuntimeUtils.isRunningMacOsX()) {
			dot = Runtime.getRuntime().exec(
					"/Applications/Graphviz.app/Contents/MacOS/dot -q5 "
							+ dotFile.getAbsolutePath());
		} else {
			dot = Runtime.getRuntime().exec(
					"dot -q5 " + dotFile.getAbsolutePath());
		}

		parser = new Parser(dot.getInputStream(), System.err);
		parser.parse();
		graph = parser.getGraph();
		parser.done_parsing();
		parser = null;

		dot.destroy();
		dot = null;
		
		return graph;

	}

}