package net.merayen.elastic.backend.context.action;

import java.io.File;
import java.util.Set;

import net.merayen.elastic.backend.context.Action;
import net.merayen.elastic.backend.data.storage.StorageView;

public class TidyProjectAction extends Action {
	@Override
	protected void run() {
		env.project.resource_manager.tidy();

		tidyStorage();
	}

	/**
	 * Compares resources with storage. If there are any files not existing in ResourceTree that exists in Storage, delete the Storage entry.
	 */
	private void tidyStorage() {
		Set<String> resources = env.project.resource_manager.getAll().keySet();

		try (StorageView sv = env.project.storage.createView()) {
			for(String m : sv.listAll(".")) {
				if(!resources.contains(m) && !m.equals("project")) {
					String p = env.project.path + "/" + m;
					System.out.println("Tidying file " + p);
					new File(p).delete();
				}
			}
		}
	}
}
