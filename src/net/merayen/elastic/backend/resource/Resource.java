package net.merayen.elastic.backend.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a resource, which can be a audio clip, midi etc.
 * When changing the underlying data, lock this Resource()-instance.
 */
public class Resource {

	/**
	 * Unique ID for this resource. This is also the path, so id can typically be:
	 * "audio/samples/somefile.wav"
	 */
	public String id;

	/**
	 * Resources this resource depends on.
	 * Resources must depend on each other, otherwise they may be deleted.
	 */
	public final Set<String> depends = new HashSet<>();

	/**
	 * Key-value properties for the resource. Free to use for external usage.
	 */
	public final Map<String, Object> data = new HashMap<>();
}
