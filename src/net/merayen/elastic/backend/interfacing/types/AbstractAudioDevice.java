package net.merayen.elastic.backend.interfacing.types;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

/**
 * Represents a single audio input interface, with perhaps support for both input and output.
 */
public abstract class AbstractAudioDevice extends AbstractDevice {
	public AbstractAudioDevice(String id, String description) {
		super(id, description);
	}

	@Override
	protected abstract void onBegin();

	/**
	 * Write audio if this device supports that.
	 * audio format is: float[<channel no>][<sample index>]
	 */
	public abstract void write(float[][] audio);

	/**
	 * Read audio if this device supports that.
	 */
	public abstract void read(float[][] output_audio);
}
