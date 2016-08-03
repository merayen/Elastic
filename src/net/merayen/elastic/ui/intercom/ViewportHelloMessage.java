package net.merayen.elastic.ui.intercom;

import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.util.Postmaster;

/**
 * Contains a list of all the current views.
 * Used by to inform the ViewportController() about which views are active.
 * This is serialized when dumped.
 */
public class ViewportHelloMessage extends Postmaster.Message {
	public final ViewportContainer viewport_container;

	public ViewportHelloMessage(ViewportContainer vc) {
		this.viewport_container = vc;
	}
}
