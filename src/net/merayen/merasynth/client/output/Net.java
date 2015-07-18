package net.merayen.merasynth.client.output;

import java.util.HashMap;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.util.AverageStat;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */

	// Tuning parameters
	private int output_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int process_buffer_size = 16; // Always try to stay minimum these many samples ahead (makes delay)
	private boolean auto_buffer_size = true; // Put option in GUI?
	private int sample_rate = 44100; // Put option in GUI?

	// These attributes changes if the input audio changes (we re-init the audio output device)
	private int channels = 0;

	private AudioOutput audio_output;
	private long last_buffer_change = 0;

	private HashMap<String, Number> statistics = new HashMap<String, Number>();

	private AverageStat<Integer> avg_buffer_size = new AverageStat<Integer>(100);

	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port, DataPacket dp) {
		if(port.equals("input")) {
			if(dp instanceof AudioResponse)
				handleAudioResponse((AudioResponse)dp);
		}
	}

	public double onUpdate() {
		if(audio_output != null) {
			int samples_in_buffer = audio_output.behind();
			if(samples_in_buffer < output_buffer_size)
				requestAudio();
		} else if(this.getPort("input") != null) { // hasPort(...) is a dirty hack until we wait for gluenode to finish initing before updating netnode
			requestAudio();
		}
		return 0.001;
	}

	// Functions called from GlueNode
	public void requestAudio() {
		AudioRequest ar = new AudioRequest();
		ar.sample_offset = 0;
		ar.sample_count = process_buffer_size;
		ar.sample_rate = sample_rate;
		this.send("input", ar);
	}

	private void initOutput(int sample_rate, int channels) {
		if(audio_output != null)
			audio_output.close();

		audio_output = new AudioOutput(sample_rate, channels);
		this.sample_rate = sample_rate;
		this.channels = channels;
		System.out.println("Inited audio device");
	}

	private void handleAudioResponse(AudioResponse ar) {
		if(ar.sample_rate != sample_rate) {
			System.out.printf("Got sample rate %d, but asked for %d. Sending node must conform to our requested sample rate.\n", ar.sample_rate, sample_rate);
			return;
		}

		if(ar.channels <= 0) {
			System.out.printf("No channels in output packet. Got %d channels.\n", channels);
			return;
		}

		if(ar.channels != channels) { // Source changes channel number. We adapt.
			initOutput(sample_rate, ar.channels);
			channels = ar.channels;
		}

		int behind = audio_output.behind();
		avg_buffer_size.add(behind);

		if(behind == 0) {
			output_buffer_size += 8;
			last_buffer_change = System.currentTimeMillis() + 1000;
		}

		audio_output.write(ar.samples);
	}

	public HashMap<String, Number> getStatistics() {
		if(audio_output != null) {
			statistics.put("sample_rate", sample_rate);
			statistics.put("channels", channels);
			statistics.put("current_buffer_size", new Double(avg_buffer_size.getAvg()).intValue());
			return statistics;
		}

		return null;
	}
}
