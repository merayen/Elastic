package net.merayen.merasynth.client.midi_input;

import net.merayen.merasynth.buffer.ObjectCircularBuffer;
import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.net.util.flow.portmanager.ProcessorManagedPort;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionHint;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.KillAllSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.process.AudioProcessor;

/**
 * This processor represents a tangent. It lives as long until all requesting nodes
 * sends EndSessionHint(), and will send data like pitch bend wheel, sustain pedal etc.
 * TODO Allow creation of sessions from right nodes too? Will send nothing?
 */
public class Processor extends AudioProcessor {
	private final ObjectCircularBuffer<MidiReceiver.MidiPacket> outgoing_midi_buffer = new ObjectCircularBuffer<>(32);
	private boolean tangent_is_down = true;
	public short midi_note_value = -1;
	public final long time_created = System.currentTimeMillis(); // Used by Net.java to find the oldest processor
	private boolean paused = true; // Call allow() to make sure we are running

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
	}

	@Override
	public void onReceive(String port_name) {
		if(port_name.equals("output"))
			tryToSendMidi();
	}

	@Override
	public void onReceiveControl(String port_name, DataPacket dp) {
		if(dp instanceof KillAllSessionsRequest) {
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

	/*
	 * We should discuss with ourself (who else) if we should really treat KEY_DOWN with velocity=0 as KEY_UP, or if we should ignore it.
	 * Currently, we treat it as KEY_UP, and we translate it like that.
	 */
	public void addMidiPacket(MidiReceiver.MidiPacket mp) {
		if(mp.midi[0] == MidiStatuses.KEY_DOWN && mp.midi[2] > 0) {
			if(midi_note_value != -1)
				throw new RuntimeException("Processor can not be assigned a note value more than once");
			midi_note_value = mp.midi[1];
			outgoing_midi_buffer.write(mp);
		} else if(mp.midi[0] == MidiStatuses.KEY_UP) {
			if(mp.midi[1] != midi_note_value)
				return; // KEY_UP event is not meant for this session, we skip this packet

			if(!tangent_is_down)
				return; // We have already sent/processed KEY_UP

			tangent_is_down = false;
			outgoing_midi_buffer.write(mp);
		} else if(mp.midi[0] == MidiStatuses.KEY_DOWN && mp.midi[2] == 0) {
			// Note that we also count KEY_DOWN with 0 in velocity as KEY_UP, as Novation LaunchPad sends this instead of KEY_UP for some reason
			if(mp.midi[1] != midi_note_value)
				return; // event is not meant for this session, we skip this packet

			if(!tangent_is_down)
				return; // We have already sent/processed KEY_UP

			tangent_is_down = false;

			// Rewrite MIDI packet to mean "KEY_UP"
			MidiReceiver.MidiPacket new_mp = new MidiReceiver.MidiPacket();
			new_mp.time = mp.time;
			new_mp.midi = new short[]{MidiStatuses.KEY_UP, mp.midi[1], 0};
			outgoing_midi_buffer.write(new_mp);
		}
	}

	public void allow() {
		if(paused) {
			respond("output", new SessionCreatedResponse()); // Notify right nodes that we have been created (also, we are now creating a processing-session)
			paused = false;
		}
	}

	public boolean isTangentDown() {
		return tangent_is_down;
	}

	private void tryToSendMidi() {
		ProcessorManagedPort pmp = this.ports.get("output");
		int to_send = pmp.buffer.available();

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