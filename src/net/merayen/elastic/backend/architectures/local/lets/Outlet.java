package net.merayen.elastic.backend.architectures.local.lets;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.Format;

public abstract class Outlet {
	public int written; // Number of samples written yet. Readers must respect this
	public final List<LocalProcessor> connected_processors = new ArrayList<>();
	public final int buffer_size;

	public Outlet(int buffer_size) {
		this.buffer_size = buffer_size;
	}

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

	public boolean satisfied() {
		if(written > buffer_size)
			throw new RuntimeException("LocalProcessor has written too much into the Outlet");

		return written == buffer_size;
	}

	public abstract Format getFormat();

	public abstract Class<? extends Inlet> getInletClass();
}
