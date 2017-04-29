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

		// Error checking
		for(DataType d : audio)
			for(float[] f : ((Audio)d).audio)
				if(f.length != samples)
					throw new RuntimeException(String.format("Expected %d samples, but got %d samples in one of the channels in one of the Audio-objects", samples, f.length));

		float[][] out = new float[channels][samples];

		for(DataType o : audio) {
			Audio a = (Audio)o;
			for(int channel_no = 0; channel_no < channels; channel_no++)
				for(int i = 0; i < samples; i++)
					out[channel_no][i] += a.audio[channel_no][i]; // Divides by 10 to get some headroom
		}

		return new Audio(out);
	}
}
