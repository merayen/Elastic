package net.merayen.elastic.system;

import java.io.IOException;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.system.actions.NewProject;
import net.merayen.elastic.system.intercom.*;

public class Test {
	public static void test() {
		new Test();
	}

	private ElasticSystem system;

	

	int fires;
	long start = System.currentTimeMillis();
	long ispinne;
	private Test() {
		system = new ElasticSystem();

		system.runAction(new NewProject("NewProject.project"));

		final long t = System.currentTimeMillis() + 1 * 2000;
		waitFor(() -> System.currentTimeMillis() > t);

		// Now just run
		startProcessing();

		system.end();
	}

	private void startProcessing() {
		final long t = System.currentTimeMillis() + 3600 * 1000;
		waitFor(() -> System.currentTimeMillis() > t);
	}

	

	private String getFirstOutputDevice(Mixer mixer) {
		for(AbstractDevice ad : mixer.getAvailableDevices()) { // Test the first output device
			if(ad instanceof AudioOutputDevice) {
				return ad.getID();
			}
		}

		throw new RuntimeException("No output audio device found");
	}

	interface Func {
		public boolean noe();
	}

	private void waitFor(Func func) {
		try {
			while(!func.noe()) {
				system.update();
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
