package net.merayen.elastic.backend.data.storage;

public class StorageFileNotFound extends StorageException {
	public StorageFileNotFound(String path) {
		super(path);
	}
}
