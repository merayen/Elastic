package net.merayen.elastic.backend.data.project;

import java.io.File;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.elastic.backend.data.resource.ResourceManager;
import net.merayen.elastic.backend.data.revision.RevisionTree;
import net.merayen.elastic.backend.data.storage.FileSystemStorage;
import net.merayen.elastic.backend.data.storage.Storage;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;
import net.merayen.elastic.netlist.NetList;

public class ProjectData {
	final Project project;

	public final Storage storage;
	public final RevisionTree revision_tree;
	public final ResourceManager resource_manager;
	private NetList netlist;

	private final boolean new_project;

	ProjectData(Project project) {
		this.project = project;

		if(!(new File(project.path)).exists()) {
			storage = new FileSystemStorage(project.path);
			revision_tree = new RevisionTree();
			resource_manager = new ResourceManager();

			DefaultProject.build(this);

			try (StorageView sv = storage.createView()) {
				save(sv);
			}

			new_project = true;
		} else { // Project exists
			storage = new FileSystemStorage(project.path);

			try (StorageView sv = storage.createView()) {
				JSONObject json_project;
				try {
					json_project = (JSONObject) new org.json.simple.parser.JSONParser().parse(new String(sv.readFile("project").read()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}

				revision_tree = net.merayen.elastic.backend.data.revision.Serializer.load((JSONObject)json_project.get("revisions")); // TODO restore from file
				resource_manager = net.merayen.elastic.backend.data.resource.Serializer.load((JSONObject)json_project.get("resources")); // TODO restore from file

				new_project = false;
			}
		}

		loadLinks();
	}

	void init() {
		if(new_project) {
			project.checkpoint.create();
		}
	}

	/**
	 * Saves everything. Should be called often, automatically.
	 * Thought: Elastic should not have a Save-feature for the user, everything goes automatically.
	 * TODO make it atomic, like, store it with another name, and then replace the current project file?
	 */
	void save(StorageView sv) {
		synchronized (this) {
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

			store(sv);
		}
	}

	/**
	 * Retrieves the current NetList based on current revision. Returned NetList
	 * can be changed which will be saved into current revision if not
	 * newRevision is called before Project().save().
	 */
	public NetList getNetList() {
		if (netlist == null) {
			if(revision_tree.getCurrent() == null) {
				netlist = new NetList();
			} else {
				String path = "netlists/" + revision_tree.getCurrent().id;
				try (StorageView sv = storage.createView()) {
					String s = new String(sv.readFile(path).read());
					netlist = net.merayen.elastic.netlist.Serializer.restore((JSONObject)new org.json.simple.parser.JSONParser().parse(s));
				} catch (ParseException e) {
					throw new RuntimeException("Failed to load NetList from project: " + path);
				}
			}
		}

		return netlist;
	}

	void tidy() {
		Set<String> resources = resource_manager.getAll().keySet();

		try (StorageView sv = storage.createView()) {
			for(String m : sv.listAll(".")) {
				if(!resources.contains(m) && !m.equals("project")) {
					String p = project.path + "/" + m;
					System.out.println("Tidying file " + p);
					new File(p).delete();
				}
			}
		}
	}

	/**
	 * Stores the current data.
	 */
	void store(StorageView sv) {
		/*StorageFile file = sv.writeFile("netlists/" + revision.id);
		file.write(net.merayen.elastic.netlist.Serializer.dump(netlist).toJSONString().getBytes(Charset.forName("UTF-8")));*/
	}

	private void loadLinks() {
		StorageView sv = storage.createView();
		String links_dir = project.path + "/links/";
		for(String v : sv.list(links_dir)) {
			StorageFile sf = sv.readFile(v);
			System.out.println("Link: " + v + " Linking to: " + new String(sf.read()));
		}
	}
}
