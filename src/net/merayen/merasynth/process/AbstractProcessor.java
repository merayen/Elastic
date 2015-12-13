package net.merayen.merasynth.process;

import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

/*
 * A processor is an one single instances that handles incoming data.
 * Each processor is assigned each session, so for a poly synth where
 * three tangents are pressed, 3 sessions will be active and do individual
 * processing.
 *
 * TODO we might need to make an adapter where we can detect active ports, which one is connected etc,
 * so that the processor knows which ports are connected etc.
 */
public abstract class AbstractProcessor {
	public final Node net_node;
	public final long session_id;
	public final long time_created = System.currentTimeMillis();

	private boolean alive = true; // Set this to false to kill this process. It will get killed eventually, but never executed again

	public AbstractProcessor(Node net_node, long session_id) {
		this.net_node = net_node;
		this.session_id = session_id;
	}

	public abstract void handle(String port_name, DataPacket dp);

	/*
	 * Called when this processor's voice is getting destroyed.
	 * Processor must deallocate any resources and inform any connected nodes that they also need to kill session.
	 * It is up to each node to figure out which ports to send the EndSessionResponse()
	 */
	public abstract void onDestroy();

	/*
	 * Class where all options to be shared between the voices.
	 * Override this with your options, if needed.
	 */
	//public static class Options {}

	/*
	 * Send your stuff through us instead of Node (net_node) directly!
	 */
	public void send(String port_name, DataPacket dp) {
		dp.session_id = session_id;
		net_node.send(port_name, dp);
	}

	public void kill() {
		alive = false;
	}

	public boolean isAlive() {
		return alive;
	}
}
