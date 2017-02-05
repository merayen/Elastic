package net.merayen.elastic.backend.resource;

import net.merayen.elastic.backend.storage.StorageView;

/**
 * A view into resources.
 * When closing this view, all open resources opened in this view will be closed.
 */
public class ResourceView {
	private final ResourceManager resource_manager;
	private final StorageView storage_view;

	ResourceView(ResourceManager rm, StorageView sv) {
		resource_manager = rm;
		storage_view = sv;
	}

	public ResourceFile getResourceFile(String id) {
		resource_manager.getResource(id);
	}

	public void closeResourceFile(ResourceFile rf) {
		
	}

	public void close() {
		
	}
}
