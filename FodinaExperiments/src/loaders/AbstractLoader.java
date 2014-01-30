package loaders;

import java.io.File;

public abstract class AbstractLoader<T> {
	protected abstract T load(File file);
}
