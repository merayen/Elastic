package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.client.signalgenerator.Net.Mode;
import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.MidiRequest;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;
import net.merayen.merasynth.netlist.util.flow.MidiFlowHelper;
import net.merayen.merasynth.process.AbstractProcessor;

/*
 * Makes beeping sounds.
 * TODO We need to rethink about data streams. We need a handler class that can help Processors to know
 * how much they should produce, like remember the amount requested, as incoming data might be much more than
 * we have requested due to ports being split.
 */
public class Processor extends AbstractProcessor {
	private final AudioFlowHelper audio_flow_helper;
	private final MidiFlowHelper midi_flow_helper;

	private float midi_amplitude = 1f;
	private float midi_frequency;

	private final Net net_node;
	private boolean dead;

	private double pos = 0;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.net_node = Net.class.cast(net_node);

		audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				//audio_flow_helper.request("frequency", request_sample_count);
				// Only sends data on first channel for now
				// TODO Send on multiple channels as given on the input port
				// TODO read data from "frequency" (if connected) and generate data dependent on that
				if(port_name.equals("output"))
					audio_flow_helper.send("output", new short[]{0}, generate(request_sample_count)); // Synchronous for now
			}

			@Override
			public void onReceive(String port_name) {
				// TODO Generate 

				System.out.printf("Data in: %d\n", audio_flow_helper.available("frequency"));
			}
		});

		audio_flow_helper.addInput("frequency");
		audio_flow_helper.addOutput("output");

		midi_flow_helper = new MidiFlowHelper(this, new MidiFlowHelper.IHandler() {

			@Override
			public void onReceive(String port_name) {
				if(port_name.equals("frequency"))
					;//System.out.printf("Got MIDI data: %s\n", midi_flow_helper.available());
			}

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// We do not output MIDI data
			}
		});

		// We tell right nodes that we have created this session and that they should now request from us
		send("output", new SessionCreatedResponse());
	}

	@Override
	public void handle(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof AudioRequest) {
				if(dead) {
					System.out.println("Got an audio request after we ended session. Ignored.");
					return;
				}

				if(net_node.isConnected("frequency")) {
					if(net_node.getMode() == Net.Mode.NONE) {
						// Mode is not set yet, we ask for both Audio and Midi
						// Depending on what we receive, we set mode after that
						requestMidi((AudioRequest)dp);
						requestAudio((AudioRequest)dp);
					}
					if(net_node.getMode() == Net.Mode.MIDI)
						requestMidi((AudioRequest)dp);
					else if(net_node.getMode() == Net.Mode.AUDIO)
						requestAudio((AudioRequest)dp);
				} else {
					audio_flow_helper.handle(port_name, dp); // Hmmm...
				}
			}
		}

		if(port_name.equals("frequency")) {
			if(!dead && dp instanceof MidiResponse) {
				if(net_node.getMode() == Net.Mode.MIDI)
					; // OK
				else if(net_node.getMode() == Net.Mode.NONE)
					net_node.setMode(Net.Mode.MIDI);
				else
					throw new RuntimeException("Processor received unexpected format on frequency-port");

				handleMidi((MidiResponse)dp);
			}

			if(!dead && dp instanceof AudioResponse) {
				if(net_node.getMode() == Net.Mode.AUDIO)
					; // OK
				else if(net_node.getMode() == Net.Mode.NONE)
					net_node.setMode(Net.Mode.AUDIO);
				else
					throw new RuntimeException("Processor received unexpected format on frequency-port");

				handleAudio((AudioResponse)dp);
			}

			if(dp instanceof EndSessionResponse)
				kill();
		}
	}

	private float[] generate(int sample_request_count) {
		// TODO mix with frequency port if it is audio
		float[] r = new float[sample_request_count]; // TODO use shared buffer
		float frequency;
		if(net_node.getMode() == Net.Mode.STANDALONE) // TODO Make several generate() functions, for MIDI, AUDIO and this for STANDALONE
			frequency = net_node.frequency;
		else if(net_node.getMode() == Net.Mode.MIDI)
			frequency = midi_frequency;
		else
			throw new RuntimeException("Not implemented yet");

		float amplitude = net_node.amplitude * midi_amplitude;

		for(int i = 0; i < r.length; i++)
			r[i] = (float)Math.sin(pos += ((Math.PI * 2.0) * frequency) / net_node.sample_rate) * amplitude * 0.2f;

		pos %= Math.PI * 2 * 1000;

		return r;
	}

	private void requestMidi(AudioRequest ar) {
		MidiRequest mr = new MidiRequest();
		mr.sample_count = ar.sample_count;
		send("frequency", mr);
	}

	private void requestAudio(AudioRequest ar) {
		send("frequency", ar);
	}

	/**
	 * Handles MIDI coming into the Frequency port.
	 * TODO better MIDI handling, for more correct time vs frequency handling.
	 */
	private void handleMidi(MidiResponse mr) {
		for(int i = 0; i < mr.midi.length; i++) {
			final short[] packet = mr.midi[i];

			if(packet[0] == MidiStatuses.KEY_DOWN) {
				setFrequencyFromMidi(packet);
			} else if(packet[0] == MidiStatuses.KEY_UP) {
				// We end session on right side. This is to save resources. User needs to latch session on the left side of us if this is not wanted.
				send("output", new EndSessionResponse());
				dead = true;
				return;
			}
		}

		// TODO don't generate audio from response sample count, we need a class that keeps track of the requested amount of data
		AudioResponse ar = new AudioResponse();
		ar.sample_count = mr.sample_count;
		ar.channels = new short[]{0}; // We only send mono
		ar.samples = generate(mr.sample_count);
		send("output", ar);
	}

	private void handleAudio(AudioResponse ar) {
		// TODO generate audio now, with the response
	}

	private void setFrequencyFromMidi(short[] packet) {
		midi_frequency = (float)(440 * Math.pow(2, (packet[1] - 69) / 12.0f));
		midi_amplitude = packet[2] / 128f;
		pos = 0;
	}

	@Override
	public void onDestroy() {
		if(!dead)
			send("output", new EndSessionResponse());
	}
}