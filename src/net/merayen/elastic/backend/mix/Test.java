package net.merayen.elastic.backend.mix;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.mix.datatypes.Audio;
import net.merayen.elastic.backend.util.SoundTest;

public class Test {
	public static void test() {
		new Test();
	}

	private Test() {
		Mixer mixer = new Mixer();

		String output_device = getFirstOutputDevice(mixer);

		mixer.reconfigure(44100, 2, 16);

		mixer.send(output_device, new Audio(new float[][]{
			SoundTest.makeSound(44100, 2f, new float[]{1000}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{1005}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{5008}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{2010}, 1f)
		}));
		mixer.dispatch(44100 * 2);
		mixer.end();

		testSynchronization();
	}

	private Synchronization sync;

	private void testSynchronization() {
		Mixer mixer = new Mixer();

		String output_device = getFirstOutputDevice(mixer);

		mixer.reconfigure(44100, 2, 16);

		final int BUFFER_SIZE = 1024;

		sync = new Synchronization(mixer, 44100, BUFFER_SIZE, new Synchronization.Handler() {

			private long last = System.currentTimeMillis();
			private long start = System.currentTimeMillis();
			private float[] audio = SoundTest.makeSound(44100, 15f, new float[]{1000}, 1f);
			private float[] output = new float[BUFFER_SIZE];
			private int position;

			int asked;

			@Override
			public void needData() {
				asked++;
				if(asked % 10 == 0)
					;//System.out.printf("Frame duration: %d ms, total duration: %d ms\n", System.currentTimeMillis() - last, System.currentTimeMillis() - start);
				last = System.currentTimeMillis();

				for(int i = 0; i < BUFFER_SIZE; i++)
					output[i] = audio[position++];

				mixer.send(output_device, new Audio(new float[][]{
					output,
					output
				}));

				mixer.dispatch(BUFFER_SIZE);

				sync.push();
				//if(asked % 5 != 0) sync.push();
			}

			@Override
			public void behind() {
				System.out.println("Lagging behind");
				//sync.push();
			}
		});

		long t = System.currentTimeMillis() + 5000;
		while(t > System.currentTimeMillis()) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mixer.end();
		sync.end();
	}

	private String getFirstOutputDevice(Mixer mixer) {
		for(AbstractDevice ad : mixer.getAvailableDevices()) { // Test the first output device
			if(ad instanceof AudioOutputDevice) {
				return ad.getID();
			}
		}

		throw new RuntimeException("No output audio device found");
	}
}
