package net.merayen.elastic.backend.storage;

public interface StorageView {
	public StorageFile readFile(String path);
	public StorageFile writeFile(String path);
	public boolean exists(String path);
	public void close();
}
