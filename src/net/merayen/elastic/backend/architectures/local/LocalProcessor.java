package net.merayen.elastic.backend.architectures.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.util.Postmaster;

public abstract class LocalProcessor {
	public enum Type {
		AUDIO(),
		MIDI()
	}

	boolean need_update = true;
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
		public final Type type;
		// Only one of these are in use
		public float[] buffer; // Audio/data. null if none is connected
		public Object[] midi;
		
		public List<Connection> connections;
		public int written; // Number of samples written yet. Readers must respect this
		private int last_written;

		Outlet(int buffer_size) {
			buffer = new float[buffer_size];
		}

		/**
		 * Return how many samples that have been output into this buffer so far.
		 */
		public int available() {
			return written;
		}
	}

	public static class Inlet extends Port {
		public final Map<Long, Outlet> outlets = new HashMap<>(); // Format: <voice_no, Outlet>. Only multiple outlets if this port does consolidate voices
		public int read; // Samples read so far. Use this to track where you are

		Inlet() {}
	}

	LocalNode localnode; // Our parent LocalNode that keeps us. TODO implement asynchronous message system
	private int buffer_size;

	/**
	 * Note that we can have multiple inlets and outlets on ports.
	 * Multiple outlets: When port can create voices
	 * Multiple inlets: When port can consolidate voices
	 */
	private final Map<String, Map<Long, Outlet>> outlets = new HashMap<>();
	private final Map<String, Map<Long, Inlet>> inlets = new HashMap<>();
	boolean keep_alive = true;

	protected abstract void onInit();
	protected abstract void onProcess();
	protected abstract void onMessage(Postmaster.Message message); // For NodeParameterChange()

	LocalProcessor() {}

	void LocalProcessor_setInfo(LocalNode localnode, int buffer_size) {
		this.localnode = localnode;
		this.buffer_size = buffer_size;

		//for(LocalNode.Port port : localnode.ports) // TODO only make PortResult for output ports?
		//	outlets.put(port.name, new Outlet(port.name, buffer_size));
	}

	void addOutputPort(String name) {
		if(outlets.containsKey(name))
			throw new RuntimeException("Port already exists");

		outlets.put(name, new HashMap<>());
	}

	void addInputPort(String name) {
		if(inlets.containsKey(name))
			throw new RuntimeException("Port already exists");

		inlets.put(name, new HashMap<>());
	}

	void addOutlet(String name, long voice_id) {
		if(!outlets.get(name).containsKey(voice_id))
			throw new RuntimeException("Outlet has already been registered");

		outlets.get(name).put(voice_id, new Outlet(buffer_size));
	}

	void addInlet(String name, long voice_id) {
		if(!inlets.get(name).containsKey(voice_id))
			throw new RuntimeException("Outlet has already been registered");

		inlets.get(name).put(voice_id, new Inlet(buffer_size));
	}

	/**
	 * Tries to retrieve a single outlet.
	 * Only call this when your output is not polyphonic.
	 */
	protected Outlet getOutlet(String port) {
		Map<Long, Outlet> p = outlets.get(port);

		if(p == null || p.size() == 0)
			return null;

		if(p.size() != 1)
			throw new RuntimeException("Multiple outlets, port might be a polyphonic port, where this call does not work");

		return p.values().iterator().next();
	}

	protected Inlet getInlet() {
		
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