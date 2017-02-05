package net.merayen.elastic.backend.data;

import net.merayen.elastic.backend.storage.StorageFile;
import net.merayen.elastic.backend.storage.StorageView;

/**
 * Builds a default project.
 * Makes a completely empty project. No nodes or anything is set up here, this should be done elsewhere (using messaging etc).
 */
class DefaultProject {
	static void build(DataManager dm) {
		StorageView sv = dm.storage.createView();
		StorageFile sf = sv.writeFile("project");
		sf.write(new byte[]{40,41,42,43,44,45,46,47,48,49});
		sv.close();
	}
}
