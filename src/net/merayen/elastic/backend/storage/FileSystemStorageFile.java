package net.merayen.elastic.backend.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Abstracts a file system file.
 * TODO Perhaps, PERHAPS, if actually necessary: implement local caching
 */
public class FileSystemStorageFile implements StorageFile {
	private RandomAccessFile raf;

	FileSystemStorageFile(String path, String mode) {
		try {
			new File(path.substring(0, path.length() - new File(path).getName().length())).mkdirs();
			this.raf = new RandomAccessFile(path, mode.equals("write") ? "rw" : "r");
		} catch (FileNotFoundException e) {
			throw new StorageException();
		}
	}

	public void read(byte[] buffer) {
		try {
			raf.read(buffer);
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	public void write(byte[] buffer) {
		try {
			raf.write(buffer);
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	public void seek(int pos) {
		try {
			raf.seek(pos);
		} catch (IOException e) {
			throw new StorageException();
		}
	}

	@Override
	public void close() throws StorageException {
		try {
			raf.close();
		} catch (IOException e) {
			throw new StorageException();
		}
	}
}
