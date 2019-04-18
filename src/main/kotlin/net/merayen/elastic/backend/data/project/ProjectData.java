package net.merayen.elastic.backend.data.project;

import java.io.File;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.elastic.backend.data.dependencygraph.DependencyGraph;
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
	public final DependencyGraph dependencyGraph;
	private NetList netlist = new NetList();

	private final boolean new_project;

	ProjectData(Project project) {
		this.project = project;

		if(!(new File(project.path)).exists()) {
			storage = new FileSystemStorage(project.path);
			revision_tree = new RevisionTree();
			dependencyGraph = new DependencyGraph();

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
					json_project = (JSONObject) new org.json.simple.parser.JSONParser().parse(new String(sv.readFile("project.json").read()));
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}

				revision_tree = net.merayen.elastic.backend.data.revision.Serializer.load((JSONObject)json_project.get("revisions")); // TODO restore from file
				dependencyGraph = net.merayen.elastic.backend.data.dependencygraph.Serializer.load((JSONObject)json_project.get("dependencygraph")); // TODO restore from file

				new_project = false;
			}
		}

		loadLinks();
	}

	void init() {
		if(new_project)
			project.checkpoint.create();
	}

	/**
	 * Saves everything. Should be called often, automatically.
	 * Thought: Elastic should not have a Save-feature for the user, everything goes automatically.
	 * TODO make it atomic, like, store it with another name, and then replace the current project file?
	 */
	synchronized void save(StorageView sv) {
		StorageFile sf = sv.writeFile("project.json");

		JSONObject project;

		try {
			project = (JSONObject)new org.json.simple.parser.JSONParser().parse(new String(sf.read()));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		JSONObject revisions = net.merayen.elastic.backend.data.revision.Serializer.dump(revision_tree);
		JSONObject dependencygraph = net.merayen.elastic.backend.data.dependencygraph.Serializer.dump(dependencyGraph);

		project.put("revisions", revisions);
		project.put("dependencygraph", dependencygraph);

		sf.seek(0);
		sf.write(project.toJSONString().getBytes());
		sf.truncate(sf.position());

		store(sv);
	}

	/**
	 * Retrieves the current NetList based on current revision. Returned NetList
	 * can be changed which will be saved into current revision if not
	 * newRevision is called before Project().save().
	 */
	public NetList getNetList() {
		return netlist;
	}

	/**
	 * Returns the current, raw NetList from the current revision.
	 * Returned NetList is always a new instance.
	 */
	public NetList getRawNetList() {
		if(revision_tree.getCurrent() == null)
			return new NetList();

		String path = "netlists/" + revision_tree.getCurrent().id + ".json";
		try (StorageView sv = storage.createView()) {
			String s = new String(sv.readFile(path).read());
			return net.merayen.elastic.netlist.Serializer.restore((JSONObject)new org.json.simple.parser.JSONParser().parse(s));
		} catch (ParseException e) {
			throw new RuntimeException("Failed to load NetList from project: " + path);
		}
	}

	void tidy() {
		Set<String> resources = dependencyGraph.getAll().keySet();

		try (StorageView sv = storage.createView()) {
			for(String m : sv.listAll(".")) {
				if(!resources.contains(m) && !m.equals("project.json")) {
					String p = project.path + "/" + m;
					System.out.println("Tidying file " + p);
					new File(p).delete();
				}
			}
		}
	}

	/**
	 * Stores the current data. Huh?
	 */
	void store(StorageView sv) {
		/*StorageFile file = sv.writeFile("netlists/" + revision.id);
		file.write(net.merayen.elastic.netlist.Serializer.dump(netlist).toJSONString().getBytes(Charset.forName("UTF-8")));*/
	}

	/**
	 * Store a new file into the project.
	 * Automatically puts a dependency to another object.
	 * @param file
	 * @param dependsOn
	 */
	public void storeFile(File file, String dependsOn) {

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
