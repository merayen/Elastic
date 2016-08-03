package net.merayen.elastic.backend.architectures.local.nodes.voices;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.net.util.flow.PortBuffer;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.MidiResponse;

/**
 * Takes care of the input-port.
 * Can only be 1 InputProcessor, and it has to be on MAIN_SESSION.
 * ...for now.
 */
public class InputProcessor extends Processor {
	private List<VoiceProcessor> voice_processors = new ArrayList<>(); // All output processors associated with this input processor
	private Net node;

	public InputProcessor(Node net_node, long session_id) {
		super(net_node, session_id);
		node = (Net)net_node;

		if(session_id != DataPacket.MAIN_SESSION)
			throw new RuntimeException("Sessioned, multiple InputProcessor not implemented yet");

		System.out.printf("InputProcessor ID %d launched\n", session_id);
	}

	@Override
	protected void onReceive(String port_name) {
		if(port_name.equals("input"))
			process();
	}

	private void process() { // TODO check that we are receiving MidiResponse on input-port
		PortBuffer input_buffer = ports.get("input").buffer;
		for(DataPacket dp : input_buffer) {
			MidiResponse mr = (MidiResponse)dp;
			for(short[] midi : mr.midi) { // TODO care about timing (mr.offset)
				if(midi[0] == MidiStatuses.KEY_DOWN) {
					VoiceProcessor vp = node.createVoiceProcessor();
					vp.tangent_value = midi[1];
					voice_processors.add(vp);
				}
			}

			for(VoiceProcessor vp : voice_processors)
				vp.process(mr); // Send data to all the voices. They filter data themselves
		}

		input_buffer.clear();
	}

	@Override
	protected void onReceiveControl(String port_name, DataPacket dp) {

	}
}
