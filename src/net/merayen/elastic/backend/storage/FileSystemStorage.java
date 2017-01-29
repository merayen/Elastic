package net.merayen.elastic.backend.storage;

import java.io.File;

/**
 * File System storage.
 */
public class FileSystemStorage extends Storage {

	private final String path;

	public FileSystemStorage(String path) {
		this.path = path;

		load();
	}

	@Override
	public void save() {
		File f = new File(path);
		if(!f.exists())
			f.mkdir();
	}

	private void load() {
		File f = new File(path);
		System.out.println(f.exists());
	}
}
