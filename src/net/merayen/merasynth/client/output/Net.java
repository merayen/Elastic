package net.merayen.merasynth.client.output;

import java.util.HashMap;
import java.util.List;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;
import net.merayen.merasynth.util.AverageStat;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */

	// Tuning parameters
	private int output_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int process_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int sample_rate = 44100; // TODO get this from event
	private AudioFlowHelper audio_flow_helper;

	// These attributes changes if the input audio changes (we re-init the audio output device)
	private int channels = 0;

	private AudioOutput audio_output;
	private long last_buffer_change = 0;

	private HashMap<String, Number> statistics = new HashMap<String, Number>();

	private AverageStat<Integer> avg_buffer_size = new AverageStat<Integer>(100);

	public Net(Supervisor supervisor) {
		super(supervisor);

		audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// Won't happen
			}

			@Override
			public void onReceive(String port_name) {
				if(audio_output == null)
					initOutput(sample_rate, 1 /* mono */); // TODO read channels to output from UI setting on this node

				handleAudioResponse();
			}
		});
	}

	@Override
	protected void onCreatePort(String port_name) {
		if(port_name.equals("input"))
			audio_flow_helper.addInput(this.getPort("input"));
	}

	protected void onReceive(String port_name, DataPacket dp) {
		audio_flow_helper.handle(port_name, dp);
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
		ar.sample_count = process_buffer_size;
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

	private void handleAudioResponse() {
		int behind = audio_output.behind();
		avg_buffer_size.add(behind);

		if(behind == 0) {
			output_buffer_size += 8;
			last_buffer_change = System.currentTimeMillis() + 1000;
		}

		AudioCircularBuffer acb = audio_flow_helper.getInputBuffer("input");
		
		// Some stupid, simple mixing to mono TODO Support multiple channels and more intelligent mixing, respecting the set output channels
		List<Short> channels = acb.getChannels();
		int channel_count = channels.size();
		int available = acb.available();
		float[] output = new float[available];
		float[] tmp_channel_buffer = new float[available];

		for(short s : channels) { // Notice: If one of the channels lags behind, it gets silent (0f)
			acb.read(s, tmp_channel_buffer);
			for(int i = 0; i < tmp_channel_buffer.length; i++)
				output[i] += tmp_channel_buffer[i] / channel_count;
		}

		audio_output.write(output);
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
