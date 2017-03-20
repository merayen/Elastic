package net.merayen.elastic.backend.data.project;

import org.json.simple.JSONObject;

import net.merayen.elastic.Info;
import net.merayen.elastic.backend.data.resource.Resource;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;

/**
 * Makes a completely empty project. No nodes or anything is set up here, this should be done elsewhere (using messaging etc).
 */
class DefaultProject {
	static void build(ProjectData dm) {
		try (StorageView sv = dm.storage.createView()) {
			StorageFile sf = sv.writeFile("project");
	
			JSONObject obj = new JSONObject();
			obj.put("storage_version", Info.storageVersion);
	
			Resource r = dm.resource_manager.create("revisions/top");
			dm.resource_manager.get("").depends.add(r); // Makes top-most resource depend on the revision, so that it doesn't disappear
	
			sf.write(obj.toJSONString().getBytes());
		}
	}
}
