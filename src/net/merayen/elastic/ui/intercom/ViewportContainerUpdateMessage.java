package net.merayen.elastic.ui.intercom;

import java.util.List;

import net.merayen.elastic.ui.objects.top.viewport.Viewport;
import net.merayen.elastic.util.Postmaster;

/**
 * Contains a list of all the current views.
 * Used by to inform the ViewportController() about which views are active.
 * This is serialized when dumped.
 */
public class ViewportContainerUpdateMessage extends Postmaster.Message {
	public final List<Viewport> viewports;

	public ViewportContainerUpdateMessage(List<Viewport> viewports) {
		this.viewports = viewports;
	}
}
