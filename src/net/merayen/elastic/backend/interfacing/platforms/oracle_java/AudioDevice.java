package net.merayen.elastic.backend.interfacing.platforms.oracle_java;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import net.merayen.elastic.backend.interfacing.types.AbstractAudioDevice;

/**
 * Wrapper for Oracle Java implementation of an audio device.
 */
public class AudioDevice extends AbstractAudioDevice {
	private final Mixer mixer;

	public AudioDevice(String id, String description, Mixer mixer) {
		super(id, description);
		this.mixer = mixer;
	}

	@Override
	protected void onBegin() {
		try {
			mixer.open();
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getBalance() {
		return 0; // TODO
	}
}
