package net.merayen.elastic.backend.interfacing.devicetypes;

public abstract class AudioInputDevice extends AudioDevice {
	public AudioInputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

	public abstract void onRead(float[] audio);

	@Override
	public boolean isOutput() {
		return false;
	}

	public final void read(float[] audio) {
		if(!isRunning())
			throw new RuntimeException("Can not read audio from device: Not running");

		if(getConfiguration() == null)
			throw new RuntimeException("Can not read audio from device: Not configured");

		onRead(audio);
	}
}
