package net.merayen.elastic.system.intercom.ui;

/**
 * Send or receive an event to/from an object in the UI.
 * 
 * Thoughts:
 * Hum... We do already do this. Should we encapsulate them? Hmm
 * These messages could be like zooming in and out (the NodeView UIObject) and so on.
 */
public class EventMessage extends UIMessage {
	public final String id; // The id for the UIObject.
	public final String key;
	public final Object data; // Must be JSON-compatible

	public EventMessage(String id, String key, Object data) {
		this.id = id;
		this.key = key;
		this.data = data;
	}
}
