package net.merayen.elastic.ui.surface;

public class Fake extends Surface {
	public Fake(String id, Handler handler) {
		super(id, handler);
	}

	/*
	 * Fake portal. Used with nodes that has its own draw canvas inside another NodeSystem.
	 */
	public int getWidth() {
		return 0; // Retrieve this from the node containing us?
	}

	public int getHeight() {
		return 0; // Retrieve this from the node containing us?
	}

	public void end() {
		// Nothing to close...
	}
}
