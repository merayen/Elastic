package net.merayen.elastic.ui.surface;

public class Fake implements Surface {
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