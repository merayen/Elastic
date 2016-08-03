package net.merayen.elastic.backend.architectures.local.nodes.voices;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.EndSessionHint;
import net.merayen.elastic.netlist.datapacket.MidiResponse;
import net.merayen.elastic.netlist.datapacket.SessionCreatedResponse;

/**
 * Only outputs on the "output" port.
 * Represents a voice on the left side.
 * XXX VoiceProcessor might be created by someone else than InputProcessor, tangent_value will be -1 then. We need to cope with this somehow!
 */
public class VoiceProcessor extends Processor {
	short tangent_value = -1;

	public VoiceProcessor(Node net_node, long session_id) {
		super(net_node, session_id);
		System.out.printf("VoiceProcessor ID %d launched\n", session_id);
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		send("output", new SessionCreatedResponse());
	}

	@Override
	protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof EndSessionHint)
				terminate();
		}
	}

	void process(MidiResponse input_mr) {
		short[][] midi = input_mr.midi;
		int[] offset = input_mr.offset;

		List<Integer> to_send = new ArrayList<>();
		MidiResponse mr = new MidiResponse();

		// Figure out which packets that fits out session
		// TODO include stuff like poly aftertouch
		for(int i = 0; i < midi.length; i++) {
			if(midi[i][0] == MidiStatuses.KEY_DOWN || midi[i][0] == MidiStatuses.KEY_UP)
				if(midi[i][1] != tangent_value)
					continue; // Packet not meant for our voice

			to_send.add(i);
		}

		// Now build our midi string
		mr.midi = new short[to_send.size()][];
		mr.offset = new int[to_send.size()];
		for(int i = 0; i < mr.midi.length; i++) {
			mr.midi[i] = midi[to_send.get(i)];
			mr.offset[i] = offset[to_send.get(i)];
		}

		mr.sample_count = input_mr.sample_count;
		send("output", mr);
	}

	@Override
	protected void onReceive(String port_name) {
		// We don't handle anything at all, also ignoring everything that comes into us at the input-port in our session, which shouldn't happen
	}
}
