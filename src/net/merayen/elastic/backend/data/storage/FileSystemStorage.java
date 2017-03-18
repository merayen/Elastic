package net.merayen.elastic.backend.data.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.merayen.elastic.backend.logicnodes.Environment;

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

	List<String> list(String path) {
		String[] result = new File(translatePath(path)).list();
		return Arrays.asList(result != null ? result : new String[0]);
	}

	List<String> listAll(String relative_path) {
		String path = translatePath(relative_path);

		List<String> result = new ArrayList<>();

		try {
			Files.walk(Paths.get(path)).sorted(Comparator.reverseOrder()).forEach((x) -> {
				if(x.toFile().isFile()) {
					String file_path = x.toFile().getAbsolutePath();

					if(!file_path.startsWith(path))
						throw new RuntimeException("Path outside project path: " + file_path);

					result.add(file_path.substring(path.length() + 1));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
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
