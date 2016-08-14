package net.merayen.elastic.backend.architectures.local.lets;

import java.util.List;

import net.merayen.elastic.backend.nodes.Format;

public abstract class Outlet {
	public int written; // Number of samples written yet. Readers must respect this
	public List<Inlet> inputs; // Inputs we are connected to

	public Outlet(int buffer_size) {}

	public void reset() {
		written = 0;
	}

	/**
	 * Notifies receiving ports about new data.
	 */
	public void push() {
		// TODO
	}

	public abstract Format getFormat();
}
