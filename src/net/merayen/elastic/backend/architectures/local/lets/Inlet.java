package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.nodes.Format;

public abstract class Inlet {
	public final Outlet outlet; // Outlet this Inlet is connected to 
	public int read; // Samples read so far. Use this to track where you are

	public Inlet(Outlet outlet) {
		this.outlet = outlet;
	}

	public void reset() {
		read = 0;
	}

	public int available() {
		return outlet.written - read;
	}

	public abstract Format getFormat();
}
