package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public class SignalInlet extends Inlet {
	public final SignalOutlet outlet;

	public SignalInlet(Outlet outlet) {
		super(outlet);
		this.outlet = (SignalOutlet)outlet;
	}

	public Format getFormat() {
		return Format.SIGNAL;
	}
}
