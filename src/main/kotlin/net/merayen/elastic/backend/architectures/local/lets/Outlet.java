package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.Format;

import java.util.ArrayList;
import java.util.List;

public abstract class Outlet extends Portlet {
	/**
	 * if the Outlet has been written to and ready to be read from.
	 */
	private boolean written;

	public final List<LocalProcessor> connected_processors = new ArrayList<>();

	/**
	 * Size of the output, in samples.
	 * TODO Maybe remove if already known by the session?
	 */
	public final int buffer_size;

	public Outlet(int buffer_size) {
		this.buffer_size = buffer_size;
	}

	@Override
	public void reset() {
		written = false;
	}

	/**
	 * Notifies receiving ports that node has written its data.
	 */
	public void push() {
		if (written)
			throw new RuntimeException("Already pushed data to Outlet");

		written = true;

		for(LocalProcessor lp : connected_processors)
			lp.schedule();
	}

	/**
	 * Returns true if the Outlet has been written to.
	 */
	public boolean available() {
		return written;
	}

	public abstract Format getFormat();

	public abstract Class<? extends Inlet> getInletClass();

	public abstract void forwardFromOutlet(Outlet source);

	public boolean satisfied() {
		return written;
	}
}