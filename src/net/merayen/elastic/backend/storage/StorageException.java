package net.merayen.elastic.backend.storage;

public class StorageException extends RuntimeException {
	public StorageException() {}

	public StorageException(String s) {
		super(s);
	}
}
