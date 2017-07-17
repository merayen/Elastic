package net.merayen.elastic.ui.event;

public class KeyboardEvent implements IEvent {
	public enum Action {
		DOWN,
		UP
	}

	private final String surface_id;
	public final char character;
	public final int code;
	public final Action action;

	public KeyboardEvent(String surface_id, char character, int code, Action action) {
		this.surface_id = surface_id;
		this.character = character;
		this.code = code;
		this.action = action;
	}

	@Override
	public String getSurfaceID() {
		return surface_id;
	}
}
