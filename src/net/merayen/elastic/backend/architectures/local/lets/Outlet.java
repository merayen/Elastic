package net.merayen.elastic.backend.architectures.local.lets;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.nodes.Format;

public abstract class Outlet {
	public int written; // Number of samples written yet. Readers must respect this
	public final List<LocalProcessor> connected_processors = new ArrayList<>();

	public Outlet(int buffer_size) {}

	public void reset() {
		written = 0;
	}

	/**
	 * Notifies receiving ports about new data.
	 */
	public void push() {
		for(LocalProcessor lp : connected_processors)
			lp.schedule();
	}

	public abstract Format getFormat();
}
