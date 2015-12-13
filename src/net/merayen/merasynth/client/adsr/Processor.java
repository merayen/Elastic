package net.merayen.merasynth.client.adsr;

import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.MidiRequest;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.process.AbstractProcessor;

public class Processor extends AbstractProcessor {
	Net net_node;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.net_node = (Net)net_node;
	}

	@Override
	public void handle(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof MidiRequest) {
				requestMidi((MidiRequest)dp);
			}
		} else if(port_name.equals("input")) {
			if(dp instanceof SessionCreatedResponse)
				send("output", dp);
			else if(dp instanceof MidiResponse)
				send("output", dp);
			else if(dp instanceof EndSessionResponse)
				kill();
		}
	}

	@Override
	public void onDestroy() {
		send("output", new EndSessionResponse());
	}

	private void requestMidi(MidiRequest mr) {
		send("input", mr);
	}
}
