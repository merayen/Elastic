package net.merayen.elastic.backend.storage.resource.types;

import net.merayen.elastic.backend.storage.resource.Resource;

/**
 * A revision represents a small or bigger change to a project.
 * Usually, when user does a CTRL-Z, we jump one revision back.
 * As Revisions are nested in a tree, different paths can be
 * created, where the user can rollback and try alternative routes.
 * 
 * Revisions are based on project. Linked projects that gets changed
 * will start a new branch in themselves, that is linked to our revision.
 * 
 * Revisions should perhaps contain delta of the change performed, so rewinding back e.g
 * 10 revisions, 10 revision needs to be executed backward. Hmm... 
 */
public class Revision extends Resource {
	/**
	 * Type of revision.
	 */
	public final String type;

	/**
	 * Revision data.
	 */
	public final Object data;

	public final Revision previous;
	public final Revision next;

	public Revision(String type, Object data, Revision previous, Revision next) {
		this.type = type;
		this.data = data;
		this.previous = previous;
		this.next = next;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void onLoad() {}
}
