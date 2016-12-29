package net.merayen.elastic.ui.event;

public class KeyboardEvent implements IEvent {
	public enum Action {
		DOWN,
		UP
	}

	public final char character;
	public final int code;
	public final Action action;

	public KeyboardEvent(char character, int code, Action action) {
		this.character = character;
		this.code = code;
		this.action = action;
	}
}
