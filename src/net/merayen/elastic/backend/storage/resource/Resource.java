package net.merayen.elastic.backend.storage.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a resource, which can be a audio clip, midi etc.
 */
public abstract class Resource {

	String id;

	/**
	 * The revision of this resource. Gets incremented if any changes are done.
	 * Synchronize() picks this up and synchronize the change over to any other Elastic
	 * instances.
	 */
	private long revision;

	/**
	 * Resources this resource depends on.
	 * Resources must depend on each other, otherwise they may be deleted.
	 */
	public final Set<Resource> depends = new HashSet<>();

	/**
	 * Key-value properties for the resource.
	 */
	public final Map<String, Object> data = new HashMap<>();

	/**
	 * Returns the size in bytes this resource allocates.
	 * Does not need to be exact, but rather close.
	 */
	public abstract int getSize();

	/**
	 * Load data from a compact byte array representation.
	 */
	protected abstract void onLoad();

	public long bumpRevision() {
		return ++revision;
	}

	public long getRevision() {
		return revision;
	}
}
