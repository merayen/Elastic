package net.merayen.elastic.backend.architectures.local.nodes.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.merayen.elastic.audio.transform.MixSessionAudio;
import net.merayen.elastic.net.util.flow.PortBuffer;
import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.AudioResponse;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;
import net.merayen.elastic.util.AverageStat;

public class Net extends AudioNode<Processor> {
	// Tuning parameters
	private int output_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int process_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int sample_rate = 44100; // TODO get this from event

	// These attributes changes if the input audio changes (we re-init the audio output device)
	private int channels = 0;

	private AudioOutput audio_output;

	private HashMap<String, Number> statistics = new HashMap<String, Number>();

	private AverageStat<Integer> avg_buffer_size = new AverageStat<Integer>(100);

	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
	}

	public double onUpdate() {
		if(audio_output != null) {
			int samples_in_buffer = audio_output.behind();
			if(samples_in_buffer < output_buffer_size) {
				requestAudio();
				tryToMix();
			}
		} else if(this.getPort("input") != null) { // hasPort(...) is a dirty hack until we wait for gluenode to finish initing before updating netnode
			requestAudio();
		}
		return 0.001;
	}

	void requestAudio() {
		request("input", new DataRequest(process_buffer_size, true), true);
	}

	private void initOutput(int sample_rate, int channels) {
		if(audio_output != null)
			audio_output.close();

		audio_output = new AudioOutput(sample_rate, channels);
		this.sample_rate = sample_rate;
		this.channels = channels;
		System.out.println("Inited audio device");
	}

	/**
	 * Called by the processors, whenever they receive audio.
	 * We then check if we can mix and output audio.
	 */
	public void notifyAudioReceived(long voice_id) {
		tryToMix();
	}

	/**
	 * Only does mono for now, and only mixes down to mono
	 */
	private void handleAudioResponse(AudioResponse ar) {
		if(audio_output == null)
			initOutput(sample_rate, 1 /* mono */); // TODO read channels to output from UI setting on this node

		int behind = audio_output.behind();
		avg_buffer_size.add(behind);

		if(behind == 0) { // Output buffer is empty, must increase our buffer!
			output_buffer_size += 8;
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

	/*
	 * Tries to mix and output audio if all the processors have data.
	 * As input can contain multiple sessions (or "voices") we need to mix it down.
	 */
	private void tryToMix() {
		if(processor_controller.activeProcesses() == 0) {
			int silence_samples = output_buffer_size - audio_output.behind();
			if(silence_samples > 0) // No processors that can actually output anything. We output some silence to not starve the output
				audio_output.write(new float[silence_samples]);
			return;
		}

		List<PortBuffer> buffers = new ArrayList<>(); // Array of all sessions/voices

		for(Processor p : processor_controller.getProcessors())
			if(p.isAlive())
				if(p.isValid())
					buffers.add(p.getBuffer());

		AudioResponse ar = MixSessionAudio.mix(buffers); // TODO send a channel map to mix down to

		if(ar != null) // Horay, mixing was possible, we output
			handleAudioResponse(ar);
	}
}