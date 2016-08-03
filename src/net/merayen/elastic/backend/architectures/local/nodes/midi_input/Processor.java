package net.merayen.elastic.backend.architectures.local.nodes.midi_input;

import net.merayen.elastic.backend.buffer.ObjectCircularBuffer;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.EndSessionHint;
import net.merayen.elastic.netlist.datapacket.EndSessionResponse;
import net.merayen.elastic.netlist.datapacket.KillAllSessionsRequest;
import net.merayen.elastic.netlist.datapacket.MidiResponse;
import net.merayen.elastic.netlist.datapacket.SessionCreatedResponse;
import net.merayen.elastic.process.AudioProcessor;

public class Processor extends AudioProcessor {
	private final ObjectCircularBuffer<MidiReceiver.MidiPacket> outgoing_midi_buffer = new ObjectCircularBuffer<>(32);

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
	}

	@Override
	protected void onCreate() {
		send("output", new SessionCreatedResponse());
	}

	@Override
	public void onReceive(String port_name) {

	}

	@Override
	public void onReceiveControl(String port_name, DataPacket dp) {
		if(dp instanceof KillAllSessionsRequest) {
			end();
			terminate(); // Some node to the right tells us to stop. This could be due to a line being disconnected etc. We stop processing.
		} else if(dp instanceof EndSessionHint) { // TODO Check if all connections to output-port has received this, as we then can terminate
			end();
			terminate(); // Terminate directly since we have no inputs
		}
	}

	@Override
	public void onDestroy() {
		respond("output", new EndSessionResponse()); // Inform right node that session is over
	}

	public void addMidiPacket(MidiReceiver.MidiPacket mp) {
		outgoing_midi_buffer.write(mp);
	}

	void tryToSendMidi(int to_send) {
		if(to_send == 0)
			return;

		MidiResponse mr = new MidiResponse();
		mr.sample_count = to_send;
		mr.midi = new short[outgoing_midi_buffer.available()][];
		mr.offset = new int[outgoing_midi_buffer.available()]; // TODO We should add timing data here, we just send everything at the same time frame like it is now

		int i = 0;
		while(outgoing_midi_buffer.available() > 0)
			mr.midi[i++] = outgoing_midi_buffer.read().midi;

		respond("output", mr);
		outgoing_midi_buffer.clear();
	}
}