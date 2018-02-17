package net.merayen.elastic.ui.surface;

import java.util.List;

import net.merayen.elastic.ui.event.UIEvent;

public abstract class Surface {
	public interface Handler {
		public void onDraw(java.awt.Graphics2D graphics2d);
	}

	private String id;
	protected Handler handler;

	public Surface(String id, Handler handler) {
		this.id = id;
		this.handler = handler;
	}

	public abstract int getWidth();
	public abstract int getHeight();

	public abstract List<UIEvent> pullEvents();

	public abstract void end();

	public String getID() {
		return id;
	}
}
