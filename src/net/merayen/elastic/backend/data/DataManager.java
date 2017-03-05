package net.merayen.elastic.backend.data;

import java.io.File;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.elastic.backend.data.resource.ResourceManager;
import net.merayen.elastic.backend.data.revision.RevisionTree;
import net.merayen.elastic.backend.data.storage.FileSystemStorage;
import net.merayen.elastic.backend.data.storage.Storage;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;

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
			storage = new FileSystemStorage(path);
			revision_tree = new RevisionTree();
			resource_manager = new ResourceManager();

			DefaultProject.build(this);

			save();
		} else { // Project exists
			storage = new FileSystemStorage(path);

			try (StorageView sv = storage.createView()) {
				JSONObject project;
				try {
					project = (JSONObject) new org.json.simple.parser.JSONParser().parse(new String(sv.readFile("project").read()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}

				revision_tree = net.merayen.elastic.backend.data.revision.Serializer.load((JSONObject)project.get("revisions")); // TODO restore from file
				resource_manager = net.merayen.elastic.backend.data.resource.Serializer.load((JSONObject)project.get("resources")); // TODO restore from file
			}
		}
	}

	/**
	 * Rewind one step back.
	 */
	public void backward() {
		
	}

	public void forward() {
		
	}

	/**
	 * Saves everything. Should be called often, automatically.
	 * Thought: Elastic should not have a Save-feature for the user, everything goes automatically.
	 */
	public void save() {
		StorageView sv = storage.createView();
		StorageFile sf = sv.writeFile("project");

		JSONObject project;

		try {
			project = (JSONObject)new org.json.simple.parser.JSONParser().parse(new String(sf.read()));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		JSONObject revisions = net.merayen.elastic.backend.data.revision.Serializer.dump(revision_tree);
		JSONObject resources = net.merayen.elastic.backend.data.resource.Serializer.dump(resource_manager);

		project.put("revisions", revisions);
		project.put("resources", resources);

		sf.seek(0);
		sf.write(project.toJSONString().getBytes());
		sf.truncate(sf.position());

		sv.close();
	}
}
