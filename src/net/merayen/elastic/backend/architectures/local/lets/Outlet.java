package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.nodes.Format;

public abstract class Outlet {
	public int written; // Number of samples written yet. Readers must respect this
	//public int last_written; // Internally to figure out if any new data has been written to this Outlet (used to notify connected Inlets)

	public Outlet(int buffer_size) {}

	public void reset() {
		written = 0;
	}

	public abstract Format getFormat();
}
