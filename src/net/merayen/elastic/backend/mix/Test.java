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

		String output_device = null;

		for(AbstractDevice ad : mixer.getAvailableDevices()) { // Test the first output device
			if(ad instanceof AudioOutputDevice) {
				output_device = ad.getID();
				break;
			}
		}

		mixer.reconfigure(44100, 2, 16);

		mixer.send(output_device, new Audio(new float[][]{
			SoundTest.makeSound(44100, 2f, new float[]{1000}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{1005}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{5008}, 1f),
			SoundTest.makeSound(44100, 2f, new float[]{2010}, 1f)
		}));
		mixer.dispatch(44100 * 2);
		mixer.stop();

		testSynchronization();
	}

	private Synchronization sync;
	public void testSynchronization() {
		Mixer mixer = new Mixer();
		sync = new Synchronization(mixer, 10, new Synchronization.Handler() {

			@Override
			public void needData(long samples_lag) {
				System.out.printf("Need data! Lag: %d\n", samples_lag);
				sync.push(10);
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

		mixer.stop();
		sync.end();
	}
}
