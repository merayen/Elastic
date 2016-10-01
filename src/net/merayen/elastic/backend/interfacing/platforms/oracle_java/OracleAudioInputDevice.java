package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;

/**
 * Wrapper for Oracle Java implementation of an audio device.
 */
public class OracleAudioInputDevice extends AudioInputDevice {
	private final TargetDataLine line;

	public OracleAudioInputDevice(Mixer mixer, TargetDataLine line) {
		super("oracle_java:" + line, "Lalala", mixer.getMixerInfo().getVendor());
		this.line = line;
	}

	@Override
	protected void onBegin() {
		try {
			line.open();
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getBalance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onKill() {
		// TODO Auto-generated method stub
		
	}
}
