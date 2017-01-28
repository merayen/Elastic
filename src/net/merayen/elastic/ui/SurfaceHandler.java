package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.surface.Surface;
import net.merayen.elastic.ui.surface.Swing;
import net.merayen.elastic.ui.util.DrawContext;

public class SurfaceHandler {
	final Map<String,Surface> surfaces = new HashMap<>();
	List<IEvent> events_queue = new ArrayList<IEvent>();
	private final Supervisor supervisor;

	SurfaceHandler(Supervisor supervisor) {
		this.supervisor = supervisor;
	}

	public void end() {
		for(Surface s: surfaces.values())
			s.end();
	}

	public Surface createSurface(String id) {
		synchronized (surfaces) {
			if(surfaces.containsKey(id))
				throw new RuntimeException("Surface with that id already exists");

			surfaces.put(id, new Swing(id, new Surface.Handler() { // TODO Hardcoded Swing as that is the only one we support for now

				@Override
				public void onEvent(IEvent event) {
					events_queue.add(event);
				}

				@Override
				public void onDraw(java.awt.Graphics2D graphics2d) {
					ArrayList<IEvent> current_events;

					synchronized (events_queue) {
						current_events = new ArrayList<>(events_queue);
						events_queue.clear();
					}

					DrawContext dc = new DrawContext(graphics2d, surfaces.get(id), current_events);				

					supervisor.draw(dc);
				}
			}));
		}

		return surfaces.get(id);
	}
}
