package net.merayen.merasynth.ui.event;

public class MouseEvent implements IEvent {
	/*
	 * Contains a mouse event
	 */

	public enum action_type {
		DOWN,
		UP,
		OVER,
		OUT,
		MOVE
	}
	
	public java.awt.event.MouseEvent mouse_event;
	public action_type action;
	
	public MouseEvent(java.awt.event.MouseEvent mouse_event, action_type action) {
		this.mouse_event = mouse_event;
		this.action = action;
	}
}
