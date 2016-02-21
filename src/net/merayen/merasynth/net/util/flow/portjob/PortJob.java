package net.merayen.merasynth.net.util.flow.portjob;

import net.merayen.merasynth.netlist.datapacket.DataPacket;

/**
 * A PortJob is an asynchronous task set on a node.
 * Usually, you send a message on the port and we wait for a reply.
 */
public abstract class PortJob {
	private PortJobManager pjm; // Set by PortJobManager when added
	public final String port_name;

	public PortJob(String port_name) {
		this.port_name = port_name;
	}

	/**
	 * Only to be called by PortJobManager
	 */
	public void setPJM(PortJobManager pjm) {
		if(this.pjm != null)
			throw new RuntimeException("Job can not be added multiple times");

		this.pjm = pjm;
	}

	/**
	 * Called when a data packet is received on the node.
	 * It is up to the inheriting node to see if packet is of interest and eventually
	 * update this job with information and call done()
	 */
	public abstract void onReceive(DataPacket dp);

	/**
	 * Called by the inheriting job when it is satisfied.
	 */
	protected void done() {
		pjm.remove(this);
	}
}
