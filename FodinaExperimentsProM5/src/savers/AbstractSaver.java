package savers;

import java.io.File;

public abstract class AbstractSaver {
	protected Object saveObject = null;
	
	public Object getSaveObject() {
		return saveObject;
	}


	public void setSaveObject(Object saveObject) {
		this.saveObject = saveObject;
	}


	protected abstract void save(File file);
}
