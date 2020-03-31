package net.merayen.elastic.backend.architectures.local.lets;

import kotlin.NotImplementedError;
import net.merayen.elastic.backend.logicnodes.Format;

public class AudioOutlet extends Outlet {
	public float[/* channel no */][/* sample index */] audio = new float[256][];
	private int channel_count;

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
		if(channel_count == this.channel_count)
			return;

		for(int i = 0; i < 256; i++)
			if(i < channel_count)
				audio[i] = new float[buffer_size];
			else
				audio[i] = null;

		this.channel_count = channel_count;
	}

	public int getChannelCount() {
		return channel_count;
	}

	@Override
	public void forwardFromOutlet(Outlet source) {
		throw new NotImplementedError();
	}
}
