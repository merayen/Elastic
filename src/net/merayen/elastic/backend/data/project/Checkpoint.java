package net.merayen.elastic.backend.data.project;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.data.resource.Resource;
import net.merayen.elastic.backend.data.revision.Revision;
import net.merayen.elastic.backend.data.storage.StorageFile;
import net.merayen.elastic.backend.data.storage.StorageView;
import net.merayen.elastic.netlist.Serializer;

public class Checkpoint {
	private final Project project;

	Checkpoint(Project project) {
		this.project = project;
	}

	public Revision create() {
		JSONObject dump = Serializer.dump(project.data.getNetList());

		Revision old_revision = project.data.revision_tree.getCurrent();
		Revision new_revision = project.data.revision_tree.create();

		Resource new_revision_resource = project.data.resource_manager.create("revisions/" + new_revision.id);
		Resource new_netlist_resource = project.data.resource_manager.create("netlists/" + new_revision.id);

		new_revision_resource.depends.add(new_netlist_resource);

		if(old_revision == null)
			project.data.resource_manager.getTop().depends.add(new_revision_resource);
		else
			project.data.resource_manager.get("revisions/" + old_revision.id).depends.add(new_revision_resource);

		// Store the NetList
		StorageView sv = project.data.storage.createView();
		StorageFile sf = sv.writeFile(new_netlist_resource.id);
		sf.write(dump.toJSONString().getBytes());

		project.data.revision_tree.setCurrent(new_revision);

		project.save();

		return new_revision;
	}

	public Revision getCurrent() {
		return project.data.revision_tree.getCurrent();
	}

	/**
	 * Shift to a certain revision.
	 */
	public void use(Revision revison) {
		project.data.revision_tree.setCurrent(revison);
	}
}
