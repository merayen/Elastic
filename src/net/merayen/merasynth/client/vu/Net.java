package net.merayen.merasynth.client.vu;

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

	private float[] channel_levels = new float[0];

	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port, DataPacket dp) {
		if(port.equals("input")) {
			if(dp instanceof AudioResponse)
				handleAudioResponse((AudioResponse)dp);
		}
	}

	private void handleAudioResponse(AudioResponse ar) {
		// TODO do some averaging
		if(ar.channels != channel_levels.length)
			channel_levels = new float[ar.channels];

		for(int i = 0; i < channel_levels.length; i++) // 1 second reduce from level 1 to 0
			channel_levels[i] -= ar.samples.length / (double)ar.sample_rate;

		for(int i = 0; i < channel_levels.length; i++) {
			
		}
	}

	public double onUpdate() {
		return DONE;
	}

	public float[] getChannelLevels() {
		return new float[] {0.7f, 0.9f, 0.3f}; // TODO do a little research on how VU is implemented in studioes (Logic Pro e.g)
		//return channel_levels;
	}
}
