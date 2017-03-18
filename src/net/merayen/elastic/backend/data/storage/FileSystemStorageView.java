package net.merayen.elastic.backend.data.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A view inside storage. Tracks all open files.
 * Open files for reading/writing. When calling close(), all open files will be closed.
 */
public class FileSystemStorageView implements StorageView {
	private final FileSystemStorage storage;
	private final List<StorageFile> open_files = new ArrayList<>();

	FileSystemStorageView(FileSystemStorage storage) {
		this.storage = storage;
	}

	public StorageFile readFile(String path) {
		StorageFile sf = storage.readFile(path);
		open_files.add(sf);
		return sf;
	}

	public StorageFile writeFile(String path) {
		StorageFile sf = storage.writeFile(path);
		open_files.add(sf);
		return sf;
	}

	public void close() {
		for(StorageFile sf : open_files)
			sf.close();
	}

	@Override
	public boolean exists(String path) {
		return storage.exists(path);
	}

	@Override
	public List<String> list(String path) {
		return storage.list(path);
	}

	@Override
	public List<String> listAll(String path) {
		return storage.listAll(path);
	}
}
