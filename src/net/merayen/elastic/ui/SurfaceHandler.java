package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.event.MouseWheelEvent;
import net.merayen.elastic.ui.surface.Surface;
import net.merayen.elastic.ui.surface.Swing;
import net.merayen.elastic.ui.util.DrawContext;

public class SurfaceHandler {
	Surface surface;
	List<IEvent> events_queue = new ArrayList<IEvent>();
	private final Supervisor supervisor;

	SurfaceHandler(Supervisor supervisor) {
		this.supervisor = supervisor;
		initSurface();
	}

	public void end() {
		surface.end();
	}

	private void initSurface() {
		surface = new Swing(new Swing.Handler() { // TODO Instantiate Fake() when not topmost nodesystem

			@Override
			public void onEvent(IEvent event) {
				events_queue.add(event);
			}

			@Override
			public void onDraw(java.awt.Graphics2D graphics2d) {
				ArrayList<net.merayen.elastic.ui.event.IEvent> current_events;

				synchronized (events_queue) {
					current_events = new ArrayList<net.merayen.elastic.ui.event.IEvent>(events_queue);
					events_queue.clear();
				}

				DrawContext dc = new DrawContext(graphics2d, surface.getWidth(), surface.getHeight(), current_events);				

				supervisor.draw(dc);
			}
		});
	}
}
