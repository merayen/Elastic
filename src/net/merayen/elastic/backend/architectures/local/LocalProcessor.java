package net.merayen.elastic.backend.architectures.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LocalProcessor {
	public static class Port {}

	public static class Connection {
		final LocalProcessor processor;
		final String port;

		Connection(LocalProcessor processor, String port) {
			this.processor = processor;
			this.port = port;
		}
	}

	public static class Outlet extends Port {
		//public final String name;
		public float[] output_buffer; // null if none is connected
		public List<Connection> connections;
		public int written; // Number of samples written yet. Readers must respect this
		private int last_written;

		Outlet(int buffer_size) {
			//this.name = port;
			output_buffer = new float[buffer_size];
		}

		/**
		 * Return how many samples that have been output into this buffer so far.
		 */
		public int available() {
			return written;
		}
	}

	//public static class Inlet extends Port {
		public final String name;
		public final Outlet output_port; // Output-port that we are connected to. null if not connected. Don't change it
		public int read; // Samples read so far. Use this to track where you are

		Inlet(Outlet p) {
			//this.name = port;
			output_port = p;
		}
	}

	LocalNode localnode; // Our parent LocalNode that keeps us. TODO implement asynchronous message system 
	private final Map<String, Outlet> outlets = new HashMap<>();
	private final Map<String, Inlet> inlets = new HashMap<>();
	boolean keep_alive = true;

	protected abstract void onInit();
	protected abstract void onProcess();

	void LocalProcessor_setInfo(LocalNode localnode, int buffer_size) {
		this.localnode = localnode;

		for(LocalNode.Port port : localnode.ports) // TODO only make PortResult for output ports?
			outlets.put(port.name, new Outlet(port.name, buffer_size));
	}

	void addPort(String name, boolean outlet) {
		if(outlets.containsKey(name))
			throw new RuntimeException("Port already exists");

		
	}

	protected Outlet getOutlet(String port) { // Feel free to cache port
		return outlets.get(port);
	}

	protected boolean isOutletConnected(String port) {
		Outlet o = outlets.get(port);
		if(o != null)
			return o.output_buffer != null;

		return false;
	}

	protected boolean isInletConnected(String port) {
		Inlet o = inlets.get(port);
		if(o != null)
			return o.output_port != null;

		return false;
	}

	protected Inlet getInlet(String port) { // Feel free to cache port
		return inlets.get(port);
	}

	/**
	 * Called when there has been data made available on one or more ports.
	 * Returns true if any data has been written
	 */
	boolean doProcess() {
		if(!dataAvailable()) // Hmm, should rather 
			return false;

		for(Outlet o : outlets.values())
			o.last_written = o.written;

		onProcess();
	}

	private boolean dataAvailable() {
		for(Inlet o : inlets.values()) {
			if(o.output_port.last_written < o.output_port.written)
				return true;
		}

		return false;
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
		keep_alive = b;
	}

	public boolean isKeepAlive() {
		return keep_alive;
	}
}
