package net.merayen.elastic.backend.data.storage;

public interface StorageFile {
	String READ = "read";
	String WRITE = "write";

	void read(byte[] buffer);
	byte[] read(); // Read everything. Be careful!
	void write(byte[] buffer);
	void truncate(long length);
	long position();
	void seek(int pos);
	void close();
}
