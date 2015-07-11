package net.merayen.merasynth.ui.objects.node;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.objects.Net;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;
import net.merayen.merasynth.ui.util.Search;

public class Port extends net.merayen.merasynth.ui.objects.Group {
	/*
	 * Connectable port
	 */
	private MouseHandler port_drag;
	public String title = "";

	private UIObject temp_port; // Used when dragging a line from this port

	protected void onInit() {
		port_drag = new MouseHandler(this);
		port_drag.setHandler(new MouseHandler.Handler() {

			@Override
			public void onMouseUp(Point position) {
				// Check with the Net-UIObject to see if a line is being drawn
				UIObject port = getNetObject().getDraggingPort(); // Retrieving the port
				if(port == null) return; // Not dragging a line from a port
				dropDraggingPort(port);
			}

			@Override
			public void onMouseDrop(Point start_point, Point offset) {
				removeTempPort();
			}

			@Override
			public void onMouseDrag(Point position, Point offset) {
				moveTempPort(position);
			}

			@Override
			public void onMouseDown(Point position) {
				// Create a new port and notifies the net
				createTempPort();
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 60, 50);
		draw.fillOval(-0.55f, -0.55f, 1.1f, 1.1f);

		draw.setColor(100, 100, 100);
		draw.fillOval(-0.5f, -0.5f, 1f, 1f);

		draw.setColor(150, 200, 150);
		draw.fillOval(-0.4f, -0.4f, 0.8f, 0.8f);

		draw.setColor(0, 0, 0);
		draw.setFont("SansSerif", 1.0f);
		draw.text(title, 1f, 0.5f);

		super.onDraw();
	}

	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		port_drag.handle(event);
	}

	private Net getNetObject() {
		Search s = new Search(search.getTopmost(), 1);
		ArrayList<UIObject> m = s.searchByType(net.merayen.merasynth.ui.objects.Net.class);
		assert m.size() == 1 : "Need exactly 1 net uiobject!";

		return (Net)m.get(0);
		// ....
	}

	private void createTempPort() {
		temp_port = new PortDrag();
		add(temp_port);
		getNetObject().addLine(this, temp_port);
		getNetObject().setDraggingPort(this);
	}

	private void moveTempPort(Point position) { // Relative coordinates
		temp_port.translation.x = position.x;
		temp_port.translation.y = position.y;
	}

	private void removeTempPort() {
		Port self = this;
		this.draw_context.queueEvent(new DelayEvent(new Runnable() {

			@Override
			public void run() {
				self.remove(temp_port);
				self.getNetObject().removeLine(self, temp_port);
				self.getNetObject().setDraggingPort(null);
				temp_port = null;
			}
		}));
	}

	private void dropDraggingPort(UIObject port) {
		if((UIObject)port == (UIObject)temp_port) return;
		if((UIObject)port == (UIObject)this) return;

		getNetObject().addLine(this, port); // Connect the ports together
	}
}
