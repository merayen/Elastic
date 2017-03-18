package net.merayen.elastic.backend.data.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a resource, which can be a audio clip, midi etc.
 * When changing the underlying data, lock this Resource()-instance.
 */
public final class Resource {

	/**
	 * Unique ID for this resource. This is also the path, so id can typically be:
	 * "audio/samples/somefile.wav"
	 */
	public final String id;

	Resource(String id) {
		this.id = id;
	}

	/**
	 * Resources this resource depends on.
	 * Resources that has no dependencies to themselves will be deleted.
	 */
	public final Set<Resource> depends = new HashSet<>();

	/**
	 * Key-value properties for the resource. Free to use. Must be JSON-compatible.
	 */
	public final Map<String, Object> data = new HashMap<>();

	public String getID() {
		return id;
	}
}