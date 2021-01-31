package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public abstract class Inlet extends Portlet {
	public final Outlet outlet; // Outlet this Inlet is connected to

	public Inlet(Outlet outlet) {
		this.outlet = outlet;
	}

	@Override
	public void reset() {}

	public abstract Format getFormat();

	/**
	 * Returns true if there is data available on this inlet.
	 */
	public boolean available() {
		return outlet.available();
	}
}
