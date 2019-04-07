package net.merayen.elastic.backend.util;

public class SoundTest {
	public static float[] makeSound(int sampleRate, float seconds, float[] frequencies, float amplitude) { // For debugging only
		float[] out = new float[(int)(sampleRate * seconds * frequencies.length)];

		for(byte channel = 0; channel < frequencies.length; channel++) {
			for(int i = 0; i < sampleRate * seconds; i++)
				out[i * frequencies.length + channel] = (float)(Math.sin((i / (double)sampleRate) * frequencies[channel] * Math.PI * 2) / 2 * amplitude);
		}

		return out;
	}
}
