package net.merayen.elastic.backend.data.storage;

import java.util.ArrayList;
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
	public String[] list(String path) {
		String[] result = storage.list(path);
		return result != null ? result : new String[0];
	}
}
