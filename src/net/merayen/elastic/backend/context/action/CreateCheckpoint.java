package net.merayen.elastic.backend.context.action;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.context.Action;
import net.merayen.elastic.backend.data.resource.Resource;
import net.merayen.elastic.backend.data.revision.Revision;
import net.merayen.elastic.netlist.Serializer;

public class CreateCheckpoint extends Action {
	@Override
	protected void run() {
		synchronized (env.project) {
			JSONObject dump = Serializer.dump(backend_context.getLogicNodeList().getNetList());
			Revision current_revision = env.project.revision_tree.getCurrent();
			Resource current_revision_resource = env.project.resource_manager.get("revisions/" + current_revision.id);
			Revision revision = env.project.revision_tree.create(current_revision);

			Resource new_revision_resource = env.project.resource_manager.create("revisions/" + revision.id);
			Resource new_netlist_resource = env.project.resource_manager.create("netlists/" + revision.id);

			// Set up dependencies
			current_revision_resource.depends.add(new_revision_resource);
			new_revision_resource.depends.add(new_netlist_resource);

			env.project.revision_tree.setCurrent(revision);

			env.project.save();
		}
	}
}
