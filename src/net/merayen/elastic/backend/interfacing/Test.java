package net.merayen.elastic.backend.interfacing;

import java.util.Map;

import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;

public class Test {
	public static void test() {
		AbstractDeviceScanner ads = Platform.getPlatformScanner(new AbstractDeviceScanner.Handler() {

			@Override
			public void onDeviceAdded(AbstractDevice device) {
				//System.out.println("Device " + device.id + " has been detected");
			}

			@Override
			public void onDeviceRemoved(AbstractDevice device) {
				//System.out.println("Device " + device.id + " has been removed");
			}
		});

		Map<String, AbstractDevice> devices = ads.getDevices();
		for(AbstractDevice ad : devices.values()) {
			if(ad instanceof AudioOutputDevice) {
				System.out.println(ad);
				AudioOutputDevice aod = (AudioOutputDevice)ad;
				aod.configure(44100, 2, 16);
				ad.begin();

				aod.write(makeSound(44100, 3, new float[]{1000, 1005}, 0.1f));

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ad.stop();
			}
		}
	}

	private static float[] makeSound(int sampleRate, int seconds, float[] frequencies, float amplitude) { // For debugging only
		float[] out = new float[sampleRate * seconds * frequencies.length];

		for(byte channel = 0; channel < frequencies.length; channel++) {
			for(int i = 0; i < sampleRate * seconds; i++)
				out[i * frequencies.length + channel] = (float)(Math.sin((i / (double)sampleRate) * frequencies[channel] * Math.PI * 2) / 2 * amplitude);
		}

		return out;
	}
}
