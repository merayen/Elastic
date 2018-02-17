package net.merayen.elastic;

import com.jssrc.resample.JSSRCResampler;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ResamplerTest2 {
	public static void main(String[] kjsfh) throws IOException {
		float[] inAudio = new float[]{
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f,
				0f,
				1f,
				0f,
				-1f
		};

		byte[] inBytes = new byte[inAudio.length * 2];
		InputStream inputStream = new ByteArrayInputStream(inBytes);

		JSSRCResampler jssrcResampler = new com.jssrc.resample.JSSRCResampler(
				new AudioFormat(100, 32,
		1, true, true),

				new AudioFormat(50, 32,
						1, true, true),

				inputStream);

		while(jssrcResampler.available() > 0)
			System.out.println(jssrcResampler.read());
	}

	private static void convertToBytes(float[] audio, byte[] out, int channels, int depth) {
		int bytes_depth = depth / 8;
		int sample_count = audio.length / channels;

		if(out.length / bytes_depth != audio.length)
			throw new RuntimeException("Invalid length of output byte-buffer. Got " + out.length + " but expected " + audio.length * bytes_depth);

		ByteBuffer buffer = ByteBuffer.allocate(channels * bytes_depth * sample_count);

		for(int i = 0; i < sample_count; i++) {
			for(byte channel = 0; channel < channels; channel++) {
				float u = audio[i * channels + channel];

				// Clipping
				if(u > 1f) u = 1f;
				else if(u < -1f) u = -1f;

				buffer.putShort((short)(u * 65535 / 2)); // Too costly?
			}
		}

		buffer.rewind();
		buffer.get(out, 0, channels * bytes_depth * sample_count);
	}
}
