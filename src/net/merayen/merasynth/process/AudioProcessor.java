package net.merayen.merasynth.process;

import net.merayen.merasynth.net.util.flow.PortBuffer;
import net.merayen.merasynth.net.util.flow.portmanager.ManagedPortState;
import net.merayen.merasynth.net.util.flow.portmanager.PortManager;
import net.merayen.merasynth.net.util.flow.portmanager.ProcessorPortManager;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.ControlRequest;
import net.merayen.merasynth.netlist.datapacket.ControlResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.ResponsePacket;

/**
 * High level processor for audio (and MIDI ++).
 * Should be inherited by most processors, unless special functionality is needed.
 */
public abstract class AudioProcessor extends AbstractProcessor {
	public ProcessorPortManager ports;
	private boolean zombie;

	public AudioProcessor(Node net_node, long session_id) {
		super(net_node, session_id);
	}

	/**
	 * Must be called to set PortManager.
	 * AudioProcessor is based on PortManager to work.
	 */
	public void setPortManager(PortManager port_manager) {
		ports = port_manager.getProcessorPortManager(this);
	}

	@Override
	public final void handle(String port_name, DataPacket dp) {
		if(dp instanceof ControlRequest || dp instanceof ControlResponse) {
			onReceiveControl(port_name, dp); 
		} else {
			ports.handle(port_name, dp);
			onReceive(port_name);
		}
	}

	/**
	 * Request data on an input port
	 */
	protected void request(String port_name, int sample_count) {
		ports.request(port_name, sample_count);
	}

	protected void respond(String port_name, ResponsePacket rp) {
		ports.respond(port_name, rp);
	}

	protected PortBuffer getPortBuffer(String port_name) {
		return ports.get(port_name).buffer;
	}

	protected ManagedPortState getPortState(String port_name) {
		return ports.get(port_name).managed_port.state;
	}

	/**
	 * Sends EndSessionHint on all connected inputs and puts the Processor in a "zombie" mode,
	 * meaning that it won't do any more processing and is just waiting to receive
	 * EndSessionResponse on all connected input ports, and then terminate.
	 */
	public void end() {
		if(zombie)
			throw new RuntimeException("end() already called");

		zombie = true;
		ports.end();
	}

	/**
	 * Terminates and removes your processor.
	 * Sends EndSessionResponse on output ports to terminate session.
	 * Note: Only call this from your processor when you have no input connections (use end() otherwise),
	 * or when all your input ports has received EndSessionResponse, we can then safely terminate
	 */
	@Override
	public void terminate() {
		// TODO check that all connected ports are actually in active==false state
		if(ports.hasActiveInputPorts())
			throw new RuntimeException("Can not terminate, node has one or more active input ports that needs to be end()'ed first");

		ports.terminate();
		super.terminate();
	}

	/**
	 * See if Processor is processing as usual.
	 */
	public boolean isAlive() {
		return !zombie && !isTerminated();
	}

	/**
	 * Your processor can implement this to get notified if any data has been received.
	 * This one is called when any data (not control packets) are received.
	 * It is then the processor's job to check if it has enough data to start processing.
	 */
	protected abstract void onReceive(String port_name);

	/**
	 * Your processor must implement this.
	 * This one is called when any control packets are received.
	 * XXX I think we can do this here, as we have said to ourself that control packets are sent out of sync (not required to be processed at correct time)
	 */
	protected abstract void onReceiveControl(String port_name, DataPacket dp);

	/*
	 * A port has been connected/disconnected.
	 * Processors needs to handle this.
	 * Every processor needs to have implementation that figures out what to do if one of the ports are connected/disconnected
	 */
	//protected abstract void onRewire(String port_name);
}
