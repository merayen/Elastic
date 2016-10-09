package net.merayen.elastic.backend.interfacing;

import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.interfacing.platforms.oracle_java.OracleAudioInputDevice;
import net.merayen.elastic.backend.interfacing.platforms.oracle_java.OracleAudioOutputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice.Configuration;
import net.merayen.elastic.backend.util.SoundTest;

public class Test {
	public static void test() {
		//testOutput();
		testNoe();
		testForwarding();
	}

	private static void testOutput() {
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

		for(AbstractDevice ad : ads.getDevices()) {
			if(ad instanceof AudioOutputDevice) {
				System.out.println("OUTPUT: " + ad.getID());

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());*/

				AudioOutputDevice aod = (AudioOutputDevice)ad;
				aod.configure(44100, 2, 16);
				ad.begin();

				aod.write(SoundTest.makeSound(44100, 1f, new float[]{1000, 1003}, 0.1f));

				aod.stop();

				return;

			} else if(ad instanceof AudioInputDevice) {
				System.out.println("INPUT: " + ad.getID());

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());
			}
		}
	}

	private static void testForwarding() {
		AbstractDeviceScanner ads = Platform.getPlatformScanner(new AbstractDeviceScanner.Handler() {
			@Override
			public void onDeviceAdded(AbstractDevice device) {}

			@Override
			public void onDeviceRemoved(AbstractDevice device) {}
		});

		AudioOutputDevice aod = null;
		for(AbstractDevice ad : ads.getDevices()) {
			if(ad instanceof AudioOutputDevice) {
				aod = (AudioOutputDevice)ad;
				break;
			}
		}

		int m = 0;
		AudioInputDevice aid = null;
		for(AbstractDevice ad : ads.getDevices()) {
			if(ad instanceof AudioInputDevice ) {
				aid = (AudioInputDevice)ad;
				if(m++ == 1)
					break;
			}
		}

		aod.configure(48000, 1, 16);
		aod.begin();

		System.out.println("OUTPUT data available now: " + aod.available());

		/*int division = 100;

		long t = System.currentTimeMillis();
		float[] silence = new float[48000 / (division * 2)];
		while(silence.length > 0) {
			for(int i = 0; i < division; i++) {
				aod.write(SoundTest.makeSound(48000, 1f / (division * 2), new float[]{1000}, 0.1f));
				aod.write(silence);
			}
			System.out.println((System.currentTimeMillis() - t) % 1000);
		}*/

		aid.configure(48000, 1, 16);
		aid.begin();

		System.out.println("INPUT data available now: " + aid.available());

		//byte[] silence = new byte[]{-50,50,50,50,50,-50,-50,-50,-50,-50,50,50,50,50,50,-50,-50,-50,-50,-50,50,50,50,50,50,-50,-50,-50,-50,-50,50,50};
		byte[] silence = new byte[1024];
		float[] audio = new float[512];
		byte[] directAudio = new byte[512*2];
		int bal = 0;
		int tx = 0, rx = 0;
		long i = 0;
		while(i++ > -1) {
			((OracleAudioInputDevice)aid).directRead(directAudio);
			rx += directAudio.length;
			/*double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
			double dc = 0;
			for(float f : audio) {
				min = Math.min(f, min);
				max = Math.max(f, max);
				dc += f;
			}

			System.out.println(min + "\t" + max + "\t\tDC: " + dc);*/

			/*int leftover = aid.getBalance();
			if(leftover > 32) {
				System.out.println("Too much leftover data " + leftover);
			}*/

			/*if(aod.available() < 128) {
				//((OracleAudioOutputDevice)aod).directWrite(silence);
				System.out.println("Balancing " + ++bal + " (" + aod.available() + " / " + aid.available() + ")");
				//tx += silence.length;
			}*/

			((OracleAudioOutputDevice)aod).directWrite(directAudio);
			tx += directAudio.length;

			/*try {
				Thread.sleep(1);
			} catch (Exception whatever) {
				break;
			}*/

			if(i % 100 == 0) {
				System.out.printf("Rx=%dk, Tx=%dk, balance=%d\n", rx / 1000, tx / 1000, tx - rx);
				rx = tx = 0;
			}
		}
	}

	private static void testNoe() {
		int o = 0;
		for(int m = 0; m < 256; m++) {
			for(int n = 0; n < 256; n++) {
				byte[] b = {(byte)m, (byte)n};
				long r = 0;
				for(int i = 0; i < b.length; i++)
					r += ((long)b[i] & 0xFF) << (b.length * 8 - i * 8 - 8);

				//System.out.println(r);// + ((long)b[1] & 0xFF) << 0);
				if(o++ != r)
					throw new RuntimeException("nei");
			}
		}
	}
}
