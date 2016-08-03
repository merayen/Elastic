package net.merayen.elastic.client.output;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioOutput {
	SourceDataLine sdl;
	private final int channels;

	public AudioOutput(int sample_rate, int channels) {
		this.channels = channels;

		try {
			sdl = AudioSystem.getSourceDataLine(new AudioFormat((float)sample_rate, 2*8, channels, true, true));
			sdl.open();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException("Can not create audio output");
		}

		sdl.start();
	}

	public void test() {
		int u = 0;

		while(1 == 1) {
			int to_write = available();
			if(to_write >= 1024) {
				float[] buff = new float[to_write];
				//System.out.printf("Skal skrive %d\n", buff.length);

				for(int i = 0; i < buff.length; i += 2) {
					buff[i + 0] = (float)Math.sin((i + u) / (44100 * 2.0) * 1000) * 1.0f; // Channel 0
					buff[i + 1] = (float)Math.sin((i + u) / (44100 * 2.0) * 2000); // Channel 1
				}
				u += buff.length;

				write(buff);
				//break;
			}
		}
	}

	/*
	 * Writes data onto buffer.
	 */
	public void write(float[] data) {
		if((data.length % channels) != 0) {
			System.out.printf("Uneven frame size. Expected %d, but got %d\n", channels, data.length % channels);
			return;
		}

		byte[] buff = new byte[2 * data.length];
		//boolean clipping = false;
		for(int i = 0; i < data.length; i++) {
			//if(data[i] > 1.0)
			//	clipping = true;

			int d = (int)((double)(data[i]) * 32700);
			if(d > 32700) d = 32700;
			if(d < -32700) d = -32700;
			buff[i * 2 + 0] = (byte)(d >> 8);
			buff[i * 2 + 1] = (byte)(d);
		}
		//if(clipping)
		//	System.out.println("Clipping");
		sdl.write(buff, 0, buff.length);
	}

	/*
	 * Returns how many samples for each channel that can be written.
	 */
	public int available() {
		int r = (sdl.available() / channels / 2);
		return r - (r % 4);
	}

	/*
	 * How many samples that are in the buffer per channel
	 */
	public int behind() {
		return (int)((sdl.getBufferSize() - sdl.available()) / 2.0 / channels);
	}

	public void close() {
		if(sdl.isOpen())
			sdl.close();
	}
}
