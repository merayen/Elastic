package net.merayen.elastic.system.intercom.system;

import net.merayen.elastic.system.intercom.backend.BackendMessage;

/**
 * Not sure, should perhaps instruct UI or Backend to do something, for testing purposes.
 */
public class FakeMessage extends BackendMessage {
	public final String key;
	public final String value;

	public FakeMessage(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
