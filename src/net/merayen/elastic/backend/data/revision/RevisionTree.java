package net.merayen.elastic.backend.data.revision;

import java.util.HashMap;
import java.util.Map;

/**
 * Base for the undo-functionality in Elastic.
 */
public class RevisionTree {
	final Map<String,Revision> list;
	Revision current;

	public RevisionTree() {
		list = new HashMap<>();

		// Add top revision
		Revision r = new Revision("top");
		list.put(r.id, r);
		current = r;
	}

	/**
	 * Create a revision.
	 */
	public Revision create(Revision previous) {
		if(getRevision(previous.id) == null)
			throw new RuntimeException("Revision not found");

		Revision r = new Revision();

		list.put(r.id, r);

		r.parent = previous;

		return r;
	}

	public Revision getRevision(String id) {
		return list.get(id);
	}

	/**
	 * Returns the active revision.
	 */
	public Revision getCurrent() {
		return current;
	}

	public void setCurrent(Revision revision) {
		if(list.get(revision.id) != revision)
			throw new RuntimeException("Invalid Revision. From another RevisionTree() or already deleted?");

		current = revision;
	}
}
