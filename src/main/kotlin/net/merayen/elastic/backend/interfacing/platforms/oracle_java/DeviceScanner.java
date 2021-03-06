package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.*;
import javax.sound.sampled.Mixer.Info;

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
		addJavaMidi();
		// TODO scan for external sources too, like VST / AUs
		// TODO add support for file output?
	}

	private void addJavaAudio() {
		for(Info i : AudioSystem.getMixerInfo()) {
			if (i.getName().contains("NVidia") || i.getName().contains("PCH"))
				continue;  // Scans slowly on Linux. Goes from 3 seconds to 150ms with this hack

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

					AbstractDevice device = new OracleAudioInputDevice(m, (TargetDataLine)target_line); // We just know that it is available
					addDevice(device);
				}
			}
		}
	}

	private void addJavaMidi() {
		for(MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			MidiDevice device;

			try {
				device = MidiSystem.getMidiDevice(info);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			if(device.getMaxTransmitters() == 0)
				continue;

			//System.out.printf("Device found: %s: %s, %s, %s, %s, %d\n", info, info.getName(), info.getDescription(), info.getVendor(), info.getVersion(), device.getMaxTransmitters());

			AbstractDevice ad = new OracleMidiInputDevice(device);

			addDevice(ad);
		}
	}
}
