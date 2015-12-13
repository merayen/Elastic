package net.merayen.merasynth.client.midi_input;

import net.merayen.merasynth.buffer.ObjectCircularBuffer;
import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.KillAllSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.MidiRequest;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.process.AbstractProcessor;

/*
 * This processor represents a tangent. It lives as long as requesting node asks to
 * destroy the session, and will send data like pitch bend wheel, sustain pedal etc,
 * until it get sent DestroySession.
 */
public class Processor extends AbstractProcessor {
	private final ObjectCircularBuffer<MidiReceiver.MidiPacket> outgoing_midi_buffer = new ObjectCircularBuffer<>(32);
	private boolean tangent_is_down = true;
	public short midi_note_value = -1;
	public final long time_created = System.currentTimeMillis(); // Used by Net.java to find the oldest processor
	private boolean paused = true; // Call begin() to make sure we are running

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
	}

	@Override
	public void handle(String port_name, DataPacket dp) {
		if(!isAlive())
			System.out.println("midi_input feil");

		if(port_name.equals("output")) {
			if(dp instanceof MidiRequest) {
				sendMidi((MidiRequest)dp);
			} else if(dp instanceof KillAllSessionsRequest) {
				kill(); // Some node to the right tells us to stop. This could be due to a line being disconnected etc. We stop processing.
			}
		}
	}

	@Override
	public void onDestroy() {
		send("output", new EndSessionResponse()); // Inform right node that session is over
	}

	public void addMidiPacket(MidiReceiver.MidiPacket mp) {
		if(mp.midi[0] == MidiStatuses.KEY_DOWN) {
			if(midi_note_value != -1)
				throw new RuntimeException("Processor can not be assigned a note value more than once");
			midi_note_value = mp.midi[1];
		} else if(mp.midi[0] == MidiStatuses.KEY_UP) {
			if(!tangent_is_down)
				return; // We have already sent/processed KEY_UP

			tangent_is_down = false;
		}

		outgoing_midi_buffer.write(mp);
	}

	public void allow() {
		if(paused) {
			send("output", new SessionCreatedResponse()); // Notify right nodes that we have been created (also, we are now creating a processing-session)
			paused = false;
		}
	}

	public boolean isTangentDown() {
		return tangent_is_down;
	}

	private void sendMidi(MidiRequest midi_request) {
		MidiResponse mr = new MidiResponse();
		mr.sample_count = midi_request.sample_count;
		mr.midi = new short[outgoing_midi_buffer.available()][];
		mr.offset = new int[outgoing_midi_buffer.available()]; // TODO We should add timing data here, we just send everything at the same time frame like it is now

		int i = 0;
		while(outgoing_midi_buffer.available() > 0)
			mr.midi[i++] = outgoing_midi_buffer.read().midi;

		send("output", mr);
		outgoing_midi_buffer.clear();
	}
}