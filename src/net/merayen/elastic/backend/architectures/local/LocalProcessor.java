package net.merayen.elastic.backend.architectures.local;

import java.util.HashMap;
import java.util.Map;

public abstract class LocalProcessor {
	private class State {
		// Is set to true when a processor wants to keep the session active
		public boolean keep_alive = false;
	}

	public static class Port {}

	public static class OutputPort extends Port {
		public final float[] output_buffer;
		int written; // Number of samples written yet

		OutputPort(int buffer_size) {
			output_buffer = new float[buffer_size];
		}

		/**
		 * Return how many samples that have been output into this buffer so far.
		 */
		public int available() {
			return written;
		}
	}

	public static class InputPort extends Port {
		public final OutputPort output_port; // Output-port that we are connected to. null if not connected

		InputPort(OutputPort p) {
			output_port = p;
		}
	}

	private static int id_counter;

	private final int id = ++id_counter;
	private final State state = new State();
	private LocalNode localnode; // Our parent LocalNode that keeps us
	protected final Map<String, PortResult> output_buffers = new HashMap<>();

	void LocalProcessor_setInfo(LocalNode localnode, int buffer_size) {
		this.localnode = localnode;

		for(LocalNode.Port port : localnode.ports) // TODO only make PortResult for output ports?
			output_buffers.put(port.name, new PortResult(buffer_size));
	}

	/**
	 * Every processor should set this.
	 * If one or more processor has this active, the session is kept alive.
	 * Not until every processor in the chain sets this to false, we will
	 * actually end the session.
	 * 
	 * It is important that every processor uses/is aware of this, as we might
	 * otherwise get stuck sessions.
	 * 
	 * E.g: A sine generator with MIDI input at frequency sets this to *true* whenever
	 * a key is pressed, and sets this to false at once on key up.
	 * 
	 * Other nodes might never set this to true and is just a slave. 
	 */
	protected void setKeepAlive(boolean b) {
		state.keep_alive = b;
	}

	public boolean isKeepingAlive() {
		return state.keep_alive;
	}
}
