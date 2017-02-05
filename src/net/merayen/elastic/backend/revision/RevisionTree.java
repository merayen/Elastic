package net.merayen.elastic.backend.revision;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for the undo-functionality in Elastic.
 */
public class RevisionTree {
	private final List<Revision> list;
	private Revision current;

	public RevisionTree() {
		list = new ArrayList<>();
		list.add(new Revision("top")); // Add top revision
		current = list.get(0);
	}

	RevisionTree(List<Revision> list, Revision current) {
		this.list = list;
		this.current = current;
	}

	/**
	 * Create a revision.
	 */
	public Revision create(Revision previous) {
		if(getRevision(previous.id) == null)
			throw new RuntimeException("Revision not found");

		Revision r = new Revision();

		previous.next = r;

		list.add(r);

		return r;
	}

	public Revision getRevision(String id) {
		for(Revision r : list)
			if(r.id.equals(id))
				return r;

		throw new RuntimeException("Revision not found");
	}

	/**
	 * Returns the active revision.
	 */
	public Revision getCurrent() {
		return current;
	}

	public void setCurrent(Revision revision) {
		current = revision;
	}
}
