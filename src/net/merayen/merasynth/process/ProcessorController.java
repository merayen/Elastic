package net.merayen.merasynth.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.merayen.merasynth.buffer.ObjectCircularBuffer;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionHint;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;

/*
 * Initiates processors and delegates data to them.
 * This makes a node being able to contain many separated processing instances of itself.
 */
public class ProcessorController<T extends AbstractProcessor> {
	public interface IHandler {
		/**
		 * Called when a processor has been created. Additional initializing of the processor is then possible.
		 */
		public void onCreate(AbstractProcessor processor);  
	}

	public static class AlreadyEndedException extends Exception {
		public AlreadyEndedException(Node net_node, long session_id) {
			super(String.format("Session can not be recreated. Node = %s, session_id = %d", net_node.getClass().getName(), session_id));
		}
	}

	private static long session_id_counter = 1; // Unique voice id

	private final int MAX_SESSIONS = 64;

	private final Map<Long,T> processors = new HashMap<>(); // XXX Replace with faster map?
	private final Class<T> process_class;
	private final IHandler handler;
	private final ObjectCircularBuffer<Long> killed_sessions = new ObjectCircularBuffer<>(1024); // Stupid check to see if someone tries to recreate a session that has been killed

	public final Node net_node;

	public ProcessorController(Node net_node, Class<T> process_class, IHandler handler) {
		this.net_node = net_node;
		this.process_class = process_class;
		this.handler = handler;
	}

	public long createProcessor() {
		try {
			return createProcessor(session_id_counter++);
		} catch(AlreadyEndedException e) {
			throw new RuntimeException(e);
		}
	}

	public long createProcessor(long voice_id) throws AlreadyEndedException {
		if(processors.size() >= MAX_SESSIONS)
			throw new RuntimeException("Too many sessions detected");

		if(voice_id < 0)
			throw new RuntimeException(String.format("Invalid session id %d", voice_id));

		if(voice_id != DataPacket.MAIN_SESSION && killed_sessions.contains(new Long(voice_id)))
			throw new AlreadyEndedException(net_node, voice_id);

		if(processors.containsKey(voice_id))
			throw new RuntimeException(String.format("Processor with session %d already exists", voice_id));

		T p;

		try {
			p = (T)process_class.getConstructor(Node.class, long.class).newInstance(net_node, voice_id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		processors.put(voice_id, p);

		handler.onCreate(p);
		p.onCreate();

		//System.out.printf("%s: Started processor %d\n", net_node.getClass().getName(), voice_id);
		return voice_id;
	}

	public void handle(String port_name, DataPacket dp) {
		purge();

		if(dp.session_id == DataPacket.CONTROL_SESSION) {
			return; // Packet not meant to be processed by processors

		} else if(dp.session_id == DataPacket.ALL_SESSIONS) {
			for(T p : processors.values())
				p.handle(port_name, dp);

		} else {
			AbstractProcessor p = processors.get(dp.session_id);
			if(p == null) {
				if(dp instanceof EndSessionHint || dp instanceof EndSessionResponse)
					return; // Don't recreate session upon receiving packets that are ending sessions

				try {
					p = processors.get(createProcessor(dp.session_id)); // Create a voice/session that handles the incoming session id
				} catch(AlreadyEndedException e) {
					System.out.println("Can not create processor: " + e.getMessage());
					return;
				}
			}

			p.handle(port_name, dp);
		}
	}

	public void killAll() {
		for(T p : processors.values())
			p.terminate();
	}

	public Collection<T> getProcessors() {
		purge();
		return processors.values();
	}

	public int activeProcesses() {
		purge();
		return processors.size();
	}

	public boolean hasProcess(long voice_id) {
		purge();
		return processors.containsKey(voice_id);
	}

	public T getProcessor(long session_id) {
		return processors.get(session_id);
	}

	private void purge() { // Maybe called too often?
		// Remove any processors that are marked to be killed
		Iterator<T> values = processors.values().iterator();
		while(values.hasNext()) {
			T p = values.next();
			if(p.isTerminated()) {
				p.onDestroy();
				values.remove();
				killed_sessions.write(new Long(p.session_id));
				//System.out.printf("%s: Destroyed processor %d\n", net_node.getClass().getName(), p.session_id);
			}
		}
	}
}