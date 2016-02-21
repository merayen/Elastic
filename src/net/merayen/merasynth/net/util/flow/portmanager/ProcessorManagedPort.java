package net.merayen.merasynth.net.util.flow.portmanager;

import net.merayen.merasynth.net.util.flow.PortBuffer;
import net.merayen.merasynth.netlist.datapacket.ControlRequest;
import net.merayen.merasynth.netlist.datapacket.ControlResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionHint;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;

public class ProcessorManagedPort {
	public final ManagedPort managed_port; // Pointer to PortManager's port definition
	public final PortBuffer buffer = new PortBuffer();
	boolean active = true; // Sets to false when receiving EndSessionHint (Stage 1)
	boolean dead = false; // Set to true when receiving EndSessionResponse (Stage 2). When all ports are dead, processor can decide to kill() itself

	ProcessorManagedPort(ManagedPort managed_port) {
		this.managed_port = managed_port;
	}

	public void handle(DataPacket dp) { // Only handles "data" packets
		if(active && !(dp instanceof ControlResponse) && !(dp instanceof ControlRequest)) {
			buffer.add(dp);
		} else if(active && dp instanceof EndSessionHint) { // Sent from this node
			active = false;
			System.out.printf("EndSessionHint received on port %s from node %s on port %s", managed_port.port_name, dp.sender_port.node.getClass().getName(), dp.sender_port.name);
		} else if(dp instanceof EndSessionResponse) { // Received by this node
			active = false;
			dead = true;
			System.out.printf("EndSessionResponse received on port %s from node %s on port %s", managed_port.port_name, dp.sender_port.node.getClass().getName(), dp.sender_port.name);
		}
	}

	public int available() {
		return buffer.available();
	}

	public boolean isActive() {
		return active;
	}

	public boolean isDead() {
		return dead;
	}
}
