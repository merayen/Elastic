package net.merayen.elastic.backend.architectures.local.nodes.in_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;

/**
 * TODO support more than just MIDI. Will probably crash and explode if anything else is given to it.
 */
public class LProcessor extends LocalProcessor {
	private MidiOutlet sourceOutlet;

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		if (frameFinished())
			return;

		if(sourceOutlet != null && sourceOutlet.satisfied())
			getOutlet("output").forwardFromOutlet(sourceOutlet);
	}

	@Override
	protected void onDestroy() {}

	/**
	 * Set by the parent node.
	 */
	void setSourceOutlet(Outlet outlet) {
		if(sourceOutlet != null) {
			sourceOutlet.connected_processors.remove(this);
			sourceOutlet = null;
		}

		if(outlet == null)
			return;

		sourceOutlet = (MidiOutlet)outlet;

		outlet.connected_processors.add(this); // Schedules us whenever the source pushes data ( push() )
	}
}