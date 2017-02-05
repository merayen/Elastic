package net.merayen.elastic.backend.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File System storage.
 */
public class FileSystemStorage implements Storage {
	private final String path;
	private final List<FileSystemStorageView> open_views = new ArrayList<>();

	public FileSystemStorage(String path) {
		try {
			this.path = new File(path).getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException("Could not get absolute path");
		}

		makeFolder(path);
	}

	private void makeFolder(String path) {
		File f = new File(path);
		if(!f.exists())
			f.mkdir();
	}

	FileSystemStorageFile readFile(String path) { // Used by FileSystemStorageView
		path = translatePath(path);

		if(!new File(path).exists())
			throw new StorageFileNotFound();

		FileSystemStorageFile v = new FileSystemStorageFile(path, StorageFile.READ);

		return v;
	}

	FileSystemStorageFile writeFile(String path) { // Used by FileSystemStorageView
		path = translatePath(path);

		makeFolder(new File(path).getParent());

		FileSystemStorageFile v = new FileSystemStorageFile(path, StorageFile.WRITE);

		return v;
	}

	boolean exists(String path) {
		return new File(translatePath(path)).exists();
	}

	@Override
	public void close() {
		for(FileSystemStorageView v : open_views)
			v.close();
	}

	@Override
	public StorageView createView() {
		FileSystemStorageView v = new FileSystemStorageView(this);
		open_views.add(v);
		return v;
	}

	private String translatePath(String path) {
		path = this.path + "/" + path;

		try {
			path = new File(path).getCanonicalPath();
		} catch (IOException e) {
			throw new StorageException("Could not get absolute path");
		}

		if(!path.startsWith(this.path))
			throw new StorageException("Path is outside project path");

		return path;
	}
}
