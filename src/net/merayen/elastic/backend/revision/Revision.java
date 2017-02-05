package net.merayen.elastic.backend.revision;

import net.merayen.elastic.util.UniqueID;

/**
 * A revision is a point in time. Is to be used by the the undo/redo feature,
 * allowing work on music project and rewinding changes/trying out different stuff.
 */
public class Revision {
	public final String id;
	public final long created;

	Revision next;
	Revision previous;

	Revision() {
		this.id = UniqueID.create();
		created = System.currentTimeMillis() / 1000;
	}

	Revision(String id) {
		this.id = id;
		this.created = System.currentTimeMillis() / 1000;
	}

	Revision(String id, long created) {
		this.id = id;
		this.created = created;
	}

	public Revision getNext() {
		return next;
	}

	public Revision getPrevious() {
		return previous;
	}
}
