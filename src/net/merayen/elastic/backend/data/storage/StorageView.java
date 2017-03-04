package net.merayen.elastic.backend.data.storage;

public interface StorageView extends AutoCloseable {
	public StorageFile readFile(String path);
	public StorageFile writeFile(String path);
	public boolean exists(String path);
	public void close();
}
