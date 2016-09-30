package net.merayen.elastic.backend.interfacing;

public class Test {
	public static void test() {
		Platform.getPlatformScanner(new AbstractDeviceScanner.Handler() {

			@Override
			public void onDeviceAdded(AbstractDevice device) {
				System.out.println("Device " + device.id + " has been detected");
			}

			@Override
			public void onDeviceRemoved(AbstractDevice device) {
				System.out.println("Device " + device.id + " has been removed");
			}
		});
	}
}
