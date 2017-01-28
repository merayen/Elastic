package net.merayen.elastic.ui.surface;

import net.merayen.elastic.ui.event.IEvent;

public abstract class Surface {
	public interface Handler {
		public void onDraw(java.awt.Graphics2D graphics2d);
		public void onEvent(IEvent event);
	}

	private String id;
	protected Handler handler;

	public Surface(String id, Handler handler) {
		this.id = id;
		this.handler = handler;
	}

	public abstract int getWidth();
	public abstract int getHeight();
	public abstract void end();

	public String getID() {
		return id;
	}
}
