package net.merayen.elastic.ui.event;

import net.merayen.elastic.util.Postmaster;

public class MessageEvent implements IEvent {
	public final Postmaster.Message message;

	public MessageEvent(Postmaster.Message message) {
		this.message = message;
	}
}
