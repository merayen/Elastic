package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public class AudioOutlet extends Outlet {
	public float[/* channel no */][/* sample index */] audio;

	public AudioOutlet(int buffer_size) {
		super(buffer_size);
		setChannelCount(0);
	}

	public Format getFormat() {
		return Format.AUDIO;
	}

	public Class<? extends Inlet> getInletClass() {
		return AudioInlet.class;
	}

	public void setChannelCount(int channel_count) {
		if(audio == null || audio.length != channel_count)
			audio = new float[channel_count][buffer_size];
	}
}
