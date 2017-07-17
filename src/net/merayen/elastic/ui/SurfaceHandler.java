package net.merayen.elastic.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.surface.Surface;
import net.merayen.elastic.ui.surface.Swing;
import net.merayen.elastic.ui.util.DrawContext;

public class SurfaceHandler {
	final Map<String,Surface> surfaces = new HashMap<>(); // A surface is usually a window
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
				public void onDraw(java.awt.Graphics2D graphics2d) {
					List<IEvent> current_events = surfaces.get(id).pullEvents();
					supervisor.draw(new DrawContext(graphics2d, surfaces.get(id), current_events));
				}
			}));
		}

		return surfaces.get(id);
	}
}
