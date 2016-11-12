package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
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

			// Speakers and other audio outputs
			for(javax.sound.sampled.Line.Info line : m.getSourceLineInfo()) {
				javax.sound.sampled.Line source_line;
				try {
					source_line = m.getLine(line);
				} catch (LineUnavailableException e) {
					throw new RuntimeException(e);
				}

				if(source_line instanceof SourceDataLine) {
					SourceDataLine sdl = (SourceDataLine)source_line;
					DataLine.Info dataline_info = (DataLine.Info)sdl.getLineInfo();

					AbstractDevice device = new OracleAudioOutputDevice(m, (SourceDataLine)source_line); // We just know that it is available
					addDevice(device);
				}
			}

			// Microphones and other audio inputs
			for(javax.sound.sampled.Line.Info line : m.getTargetLineInfo()) {
				javax.sound.sampled.Line target_line;
				try {
					target_line = m.getLine(line);
				} catch (LineUnavailableException e) {
					throw new RuntimeException(e);
				}

				if(target_line instanceof TargetDataLine) {
					TargetDataLine sdl = (TargetDataLine)target_line;
					DataLine.Info dataline_info = (DataLine.Info)sdl.getLineInfo();

					AbstractDevice device = new OracleAudioInputDevice(m, (TargetDataLine)target_line); // We just know that it is available
					addDevice(device);
				}
			}
		}
	}
}