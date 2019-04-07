package net.merayen.elastic;

import com.laszlosystems.libresample4j.Resampler;

import java.nio.FloatBuffer;

public class ResamplerTest {
	public static void main(String[] fskjhfjksd) {
		float[] inAudio = new float[100];

		sine(inAudio, 2);
		FloatBuffer inBuffer = FloatBuffer.wrap(inAudio);
		inBuffer.put(inAudio);

		float factor = 10f;
		int outLength = (int)Math.ceil(inAudio.length * factor);
		FloatBuffer outBuffer = FloatBuffer.allocate(outLength);

		Resampler resampler = null;
		long time = System.nanoTime();
		for(int i = 0; i < 100; i++)
			resampler = new Resampler(true, 0.0013, 767);
		System.out.printf("Initializing: %.3fms\n", (System.nanoTime() - time) / 1E6);

		time = System.nanoTime();
		for(int i = 0; i < 1000; i++) {
			inBuffer.rewind();
			outBuffer.rewind();
			resampler.process(factor, inBuffer, false, outBuffer);
			if(outBuffer.position() > 0) {
				System.out.println(i + " " + outBuffer.position());
				break;
			}
		}
		time -= System.nanoTime();

		System.out.println();

		for(int i = 0; i < outLength; i++) {
			for (float j = -2f; j <= 2f; j += 1 / 50f) {
				if (j == -2f) {
					System.out.printf("%4d", i);
				} else if (outBuffer.get(i) / 100 > j) {
					System.out.print(" ");
				} else {
					System.out.print(String.format("%.1f", outBuffer.get(i)));
					break;
				}
			}
			System.out.println();
		}

		System.out.println(-time / 1E6);
	}

	class ResampleBox {
		private Resampler resampler;

		ResampleBox(float minFactor, float maxFactor, float[] inBuffer, float[] outBuffer) {
			resampler = new Resampler(true, minFactor, maxFactor);
		}
	}

	private static void sine(float[] output, float divider) {
		for(int i = 0; i < output.length; i++)
			output[i] = ((float)Math.sin((i * Math.PI) / divider) * 100);
	}
}
