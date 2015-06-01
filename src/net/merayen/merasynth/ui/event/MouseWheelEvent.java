package net.merayen.merasynth.ui.event;

import net.merayen.merasynth.ui.event.MouseEvent.action_type;

public class MouseWheelEvent implements IEvent {
	private java.awt.event.MouseWheelEvent mousewheel_event;
	
	public MouseWheelEvent(java.awt.event.MouseWheelEvent event) {
		this.mousewheel_event = event;
	}
	
	public int getOffsetX() {
		return 0;
	}
	
	public int getOffsetY() {
		return this.mousewheel_event.getWheelRotation();
	}
}
