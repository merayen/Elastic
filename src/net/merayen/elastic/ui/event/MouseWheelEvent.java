package net.merayen.elastic.ui.event;

public class MouseWheelEvent implements IEvent {
	private final String surface_id;
	private java.awt.event.MouseWheelEvent mousewheel_event;
	
	public MouseWheelEvent(String surface_id, java.awt.event.MouseWheelEvent event) {
		this.surface_id = surface_id;
		this.mousewheel_event = event;
	}
	
	public int getOffsetX() {
		return 0;
	}
	
	public int getOffsetY() {
		return this.mousewheel_event.getWheelRotation();
	}

	@Override
	public String getSurfaceID() {
		return surface_id;
	}
}
