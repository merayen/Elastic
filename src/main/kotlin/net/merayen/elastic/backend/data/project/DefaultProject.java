package net.merayen.elastic.backend.data.project;

import net.merayen.elastic.backend.data.dependencygraph.DependencyItem;
import org.json.simple.JSONObject;

import net.merayen.elastic.Info;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;

/**
 * Makes a completely empty project. No nodes or anything is set up here, this should be done elsewhere (using messaging etc).
 */
class DefaultProject {
	static void build(ProjectData dm) {
		try (StorageView sv = dm.storage.createView()) {
			StorageFile sf = sv.writeFile("project.json");
	
			JSONObject obj = new JSONObject();
			obj.put("storage_version", Info.storageVersion);
	
			DependencyItem r = dm.dependencyGraph.create("revisions/top");
			dm.dependencyGraph.get("").getDependsOn().add(r); // Makes top-most dependencygraph depend on the revision, so that it doesn't disappear
	
			sf.write(obj.toJSONString().getBytes());
		}
	}
}
