package net.merayen.elastic.backend.storage;

public interface StorageFile {
	String READ = "read";
	String WRITE = "write";

	void read(byte[] buffer);
	void write(byte[] buffer);
	void seek(int pos);
	void close();
}
