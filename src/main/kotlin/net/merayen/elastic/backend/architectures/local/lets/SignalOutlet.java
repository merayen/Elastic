package net.merayen.elastic.backend.architectures.local.lets;

import kotlin.NotImplementedError;
import net.merayen.elastic.backend.logicnodes.Format;

public class SignalOutlet extends Outlet {
	public final float[/* sample index */] signal;
	private int channel_count;

	public SignalOutlet(int buffer_size) {
		super(buffer_size);
		signal = new float[buffer_size];
	}

	public Format getFormat() {
		return Format.SIGNAL;
	}

	public Class<? extends Inlet> getInletClass() {
		return SignalInlet.class;
	}

	@Override
	public void forwardFromOutlet(Outlet source) {
		throw new NotImplementedError();
	}
}
