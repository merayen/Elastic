package net.merayen.elastic.backend.data.storage;

import java.util.List;

public interface StorageView extends AutoCloseable {
	public StorageFile readFile(String path);
	public StorageFile writeFile(String path);
	public boolean exists(String path);
	public List<String> list(String path);
	public List<String> listAll(String path);
	public void close();
}
