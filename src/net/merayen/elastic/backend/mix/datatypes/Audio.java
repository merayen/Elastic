package net.merayen.elastic.backend.mix.datatypes;

import java.util.List;

public class Audio extends DataType {
	public final float[/* channel no */][] audio;

	public Audio(float[][] audio) {
		this.audio = audio;
	}

	static Audio mix(int samples, List<DataType> audio) {

		// Detect count of channels to mix
		int channels = 0;
		for(DataType a : audio)
			channels = Math.max(channels, ((Audio)a).audio.length);

		float[][] out = new float[channels][samples];

		for(DataType o : audio) {
			Audio a = (Audio)o;
			for(int channel_no = 0; channel_no < a.audio.length; channel_no++)
				for(int i = 0; i < a.audio[channel_no].length; i++)
					out[channel_no][i] += a.audio[channel_no][i] / 10f; // Divides by 10 to get some headroom
		}

		return new Audio(out);
	}
}
