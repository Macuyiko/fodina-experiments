package org.processmining.framework.ui;

public class Progress implements CancelationComponent {
	public Progress(String caption) {
		this(caption, 0, 100);
	}

	public Progress(int minimum, int maximum) {
		this("", minimum, maximum);
	}

	public Progress(String caption, int minimum, int maximum) {
		System.err.println("Using dummy progress");
	}

	protected Progress() {
	}

	public void close() {

	}

	public boolean isCanceled() {
		return false;
	}

	public void setMaximum(int m) {

	}

	public int getMaximum() {
		return 100;
	}

	public void setMinimum(int m) {

	}

	public void setMinMax(int min, int max) {

	}

	public void setNote(String note) {
		System.err.println(note);
	}

	public String getNote() {
		return "";
	}

	public void setProgress(int nv) {

	}

	public void inc() {
		
	}

	protected void doUpdate(Runnable r) {
		
	}
}