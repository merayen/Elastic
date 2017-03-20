package net.merayen.elastic.backend.data.project;

import net.merayen.elastic.backend.data.storage.StorageView;
import net.merayen.elastic.netlist.NetList;

/**
 * Holds storage, revisions and resources. Use Accessor() to retrieve and set
 * data to the Project.
 */
public class Project {
	public final String path;
	public final Checkpoint checkpoint = new Checkpoint(this);

	/**
	 * Careful by using this directly, should probably use methods in Project().
	 */
	public final ProjectData data;

	/**
	 * Loads or creates a new data storage on the designated path.
	 */
	public Project(String path) {
		this.path = path;
		data = new ProjectData(this);

		data.init();
	}

	/**
	 * Retrieve the current active NetList. Feel free to change it, though
	 * calling save() will overwrite the current NetList. Do checkpoint.create()
	 * first.
	 */
	public NetList getNetList() {
		return data.getNetList();
	}

	public void save() {
		try (StorageView sv = data.storage.createView()) {
			data.save(sv);
		}
	}

	public void tidy() {
		data.tidy();
	}
}
