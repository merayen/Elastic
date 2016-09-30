package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner;
import net.merayen.elastic.backend.interfacing.types.AbstractAudioDevice;
import net.merayen.elastic.backend.interfacing.types.AudioOutputDevice;
import net.merayen.elastic.backend.interfacing.AbstractDevice;

/**
 * Scanner for the Oracle/OpenJDK JRE.
 */
public class DeviceScanner extends AbstractDeviceScanner {
	public DeviceScanner(Handler handler) {
		super(handler);
		scan();
	}

	private void scan() {
		addJavaAudio();
		// TODO scan for external sources too, like VST / AUs. Logic should be centralized, not live inside this oracle_java
	}

	private void addJavaAudio() {
		for(Info i : AudioSystem.getMixerInfo()) {
			Mixer m = AudioSystem.getMixer(i);
			Line.Info line_info = m.getLineInfo();
			/*try {
				m.open();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

			System.out.println(i.getName());
			for(javax.sound.sampled.Line.Info line : m.getSourceLineInfo()) {
				System.out.println("\tSource: " + line);
				//AbstractDevice device = new AudioInputDevice("oracle_java:java_audio:" + line.toString(), "Oracle Java: " + i.getDescription()); // We just know that it is available
				//addDevice(device);
			}

			for(javax.sound.sampled.Line.Info line : m.getTargetLineInfo()) {
				//AbstractDevice device = new AudioOutputDevice("oracle_java:java_audio:" + line.toString(), "Oracle Java: " + i.getDescription()); // We just know that it is available
				//addDevice(device);
				System.out.println("\tTarget: " + line);
			}
		}
	}
}
