package net.merayen.elastic.backend.util;

import java.util.Arrays;

public class AudioUtil {
	/**
	 * Merges two channels of audio to a single array of floats.
	 */
	public static void mergeChannels(float[][] input, float[] output, int sample_count, int channel_count) {
		if(output.length != channel_count * sample_count)
			throw new RuntimeException("Output array has wrong size. Is " + output.length + " but should have been " + channel_count * sample_count);

		Arrays.fill(output, 0);

		for(int channel_no = 0; channel_no < channel_count; channel_no++) {
			if(input.length <= channel_no || input[channel_no] == null)
				continue;

			if(input[channel_no].length != sample_count)
				throw new RuntimeException("input channel does not have enough/too many samples");

			for(int i = 0; i < sample_count; i++)
				output[i * channel_count + channel_no] = input[channel_no][i];
		}
	}

	public static double midiNoteToFreq(float n) {
		return 440 * Math.pow(2, (n - 69) / 12.0f);
	}
}
