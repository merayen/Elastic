package net.merayen.elastic.backend.interfacing;

import net.merayen.elastic.backend.interfacing.devicetypes.AudioDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.util.SoundTest;

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

		for(AbstractDevice ad : ads.getDevices()) {
			if(ad instanceof AudioOutputDevice) {
				System.out.println("OUTPUT: " + ad.getID());

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());*/

				AudioOutputDevice aod = (AudioOutputDevice)ad;
				aod.configure(44100, 4, 16);
				ad.begin();

				aod.write(SoundTest.makeSound(44100, 1f, new float[]{1000, 1001, 1002, 1003}, 0.1f));

				aod.stop();

			} else if(ad instanceof AudioInputDevice) {
				System.out.println("INPUT: " + ad.getID());

				//for(AbstractDevice.Configuration c : ad.getAvailableConfigurations())
				//	System.out.println(((AudioDevice.Configuration)c).getDescription());
			}
		}
	}
}
