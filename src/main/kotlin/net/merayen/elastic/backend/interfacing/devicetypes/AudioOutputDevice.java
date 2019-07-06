package net.merayen.elastic.backend.interfacing.devicetypes;

public abstract class AudioOutputDevice extends AudioDevice {
	public AudioOutputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

	public abstract void onWrite(float[] audio);
	public abstract void onWrite(float[][] audio);

	public final void write(float[] audio) {
		if(!isRunning())
			throw new RuntimeException("Can not write audio to device: Not running");

		if(getConfiguration() == null)
			throw new RuntimeException("Can not write audio to device: Not configured");

		onWrite(audio);
	}

	public final void write(float[][] audio) {
		if(!isRunning())
			throw new RuntimeException("Can not write audio to device: Not running");

		if(getConfiguration() == null)
			throw new RuntimeException("Can not write audio to device: Not configured");

		//if(((AudioDevice.Configuration)configuration).channels != audio.length)
		//	throw new RuntimeException("E");

		onWrite(audio);
	}

	@Override
	public final boolean isOutput() {
		return true;
	}
}
