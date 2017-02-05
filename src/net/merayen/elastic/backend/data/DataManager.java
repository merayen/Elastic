package net.merayen.elastic.backend.data;

import java.io.File;

import net.merayen.elastic.backend.resource.ResourceManager;
import net.merayen.elastic.backend.revision.RevisionTree;
import net.merayen.elastic.backend.storage.FileSystemStorage;
import net.merayen.elastic.backend.storage.Storage;

/**
 * Takes care off storage, revisions and resources.
 */
public class DataManager {
	public final Storage storage;
	public final RevisionTree revision_tree;
	public final ResourceManager resource_manager;

	/**
	 * Loads or creates a new data storage on the designated path.
	 */
	public DataManager(String path) {
		if(!(new File(path)).exists()) {
			this.storage = new FileSystemStorage(path);
			this.revision_tree = new RevisionTree();
			this.resource_manager = new ResourceManager();

			DefaultProject.build(this);
		} else { // Project exists
			this.storage = new FileSystemStorage(path);
			this.revision_tree = new RevisionTree(); // TODO restore from file
			this.resource_manager = new ResourceManager(); // TODO restore from file
		}
	}
}
