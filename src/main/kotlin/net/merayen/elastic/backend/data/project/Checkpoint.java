package net.merayen.elastic.backend.data.project;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.data.dependencygraph.DependencyItem;
import net.merayen.elastic.backend.data.revision.Revision;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;
import net.merayen.elastic.netlist.Serializer;

public class Checkpoint {
	private final Project project;

	Checkpoint(Project project) {
		this.project = project;
	}

	/**
	 * Create a checkpoint
	 */
	public Revision create() {
		JSONObject dump = Serializer.dump(project.getData().getNetList());

		Revision old_revision = project.getData().revision_tree.getCurrent();
		Revision new_revision = project.getData().revision_tree.create();

		DependencyItem new_revision_dependencyItem = project.getData().dependencyGraph.create("revisions/" + new_revision.id + ".json");
		DependencyItem new_netlist_dependencyItem = project.getData().dependencyGraph.create("netlists/" + new_revision.id + ".json");

		new_revision_dependencyItem.getDependsOn().add(new_netlist_dependencyItem);

		if (old_revision == null)
			project.getData().dependencyGraph.getTop().getDependsOn().add(new_revision_dependencyItem);
		else
			project.getData().dependencyGraph.get("revisions/" + old_revision.id + ".json").getDependsOn().add(new_revision_dependencyItem);

		// Store the NetList
		try (StorageView sv = project.getData().storage.createView()) {
			StorageFile sf = sv.writeFile(new_netlist_dependencyItem.getId());
			sf.write(dump.toJSONString().getBytes());
		}

		project.getData().revision_tree.setCurrent(new_revision);

		project.save();

		return new_revision;
	}

	public Revision getCurrent() {
		return project.getData().revision_tree.getCurrent();
	}

	/**
	 * Shift to a certain revision.
	 */
	public void use(Revision revision) {
		project.getData().revision_tree.setCurrent(revision);
	}
}
