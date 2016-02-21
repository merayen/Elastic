package net.merayen.merasynth.net.util.flow.portmanager;

import java.util.List;

import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.DataRequest;
import net.merayen.merasynth.netlist.datapacket.EndSessionHint;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.ResponsePacket;
import net.merayen.merasynth.process.AbstractProcessor;

/**
 * "Subclass" from PortManager.
 * Used by processors.
 */
public class ProcessorPortManager {
	private final AbstractProcessor processor;
	private final ProcessorManagedPort[] ports;
	private boolean alive = true;

	public ProcessorPortManager(PortManager port_manager, AbstractProcessor processor, List<ManagedPort> ports) {
		this.processor = processor;

		this.ports = new ProcessorManagedPort[ports.size()]; // Create our own ports from our parent PortManager
		for(int i = 0; i < this.ports.length; i++)
			this.ports[i] = new ProcessorManagedPort(ports.get(i));
	}

	/**
	 * Processor calls this method to send data on output port.
	 */
	public void respond(String port_name, ResponsePacket rp) {
		ProcessorManagedPort pmp = get(port_name);

		pmp.managed_port.confirmSend(rp);
		processor.send(port_name, rp);
	}

	/**
	 * Processor calls this method to request data
	 */
	public void request(String port_name, int sample_count) {
		ProcessorManagedPort pmp = get(port_name);

		//if(!pmp.buffer.isEmpty()) // Disabled for now as it would be extra work for the node to make sure it synchronizes correctly between all inputs
		//	throw new RuntimeException("Programming error. Can't do request when there is data available in the buffer");

		DataRequest dr = new DataRequest();
		dr.sample_count = sample_count;

		pmp.managed_port.confirmSend(dr);

		processor.send(port_name, dr);
	}

	public void handle(String port_name, DataPacket rp) {
		if(!alive)
			return;

		ProcessorManagedPort pmp = get(port_name);

		pmp.managed_port.confirmReceive(rp);

		pmp.handle(rp); // Both responses and requests
	}

	public ProcessorManagedPort get(String port_name) {
		for(int i = 0; i < ports.length; i++)
			if(ports[i].managed_port.port_name.equals(port_name))
				return ports[i];

		throw new RuntimeException("Port not found: " + port_name);
	}

	public ProcessorManagedPort[] getPorts() {
		return ports;
	}

	/**
	 * Call this right before the processor is terminated.
	 * This notifies every connected node on the left side of us that we are ending session.
	 * Processor will by this go into "zombie"-mode, where it exists, but does no processing,
	 * other than waiting for left nodes to send EndSessionResponse.
	 */
	public void end() {
		alive = false;

		for(ProcessorManagedPort pmp : ports) {
			if(pmp.active) {
				if(pmp.managed_port.state != null && !pmp.managed_port.state.output)
					processor.send(pmp.managed_port.port_name, new EndSessionHint() );

				pmp.active = false;
				pmp.buffer.clear(); //All remaining data is cleared
			}
		}
	}

	/**
	 * Call this when node should be ending, meaning that it will notify all output ports that it is now actually terminating.
	 * All input ports must be dead, or no input ports connected to be able to terminate.
	 * Not to be called directly.
	 */
	public void terminate() {
		if(alive)
			throw new RuntimeException("end() must be called first");

		for(ProcessorManagedPort pmp : ports) {
			if(pmp.managed_port.state != null) {
				if(pmp.managed_port.state.output)
					if(!pmp.dead)				
						processor.send(pmp.managed_port.port_name, new EndSessionResponse());
			}
			pmp.dead = true; // XXX We mark ports with no state as dead anyway, this might happen if port is not connected or if some connected port never initiated handshake (which is an error?)
		}
	}

	public boolean hasActiveInputPorts() {
		for(ProcessorManagedPort pmp : ports)
			if(pmp.active && pmp.managed_port.connection_count > 0)
				return true;

		return false;
	}
}
