package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent.Button;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.Point;

public class UIPortTemporary extends UIPort {
	/*
	 * This port is created when user starts to drag a line from a port.
	 * Is invisible.
	 * Only to be used by UIPort internally!
	 */
	private UIPort source_port;
	public UIPort target; // Set by the port which the port is hanging over
	private MouseHandler mouse;
	private Point original;

	public UIPortTemporary(UINode uinode) {
		super("temporary_dragging_port", false, uinode);
		draw_default_port = false;

		mouse = new MouseHandler(this, Button.LEFT);
		mouse.setHandler(new MouseHandler.Handler() {
			@Override
			public void onGlobalMouseUp(Point global_position) {
				removeTempPort();
			}

			@Override
			public void onGlobalMouseMove(Point position) {
				if(original == null)
					original = new Point(position.x, position.y);

				moveTempPort(new Point(position.x, position.y));
			}

			@Override
			public void onGlobalMouseUp(Point position) {
				
			}
		});
	}

	public void addTempPort(UIPort source_port) { // Must be called after you have added this class via add(...)
		this.source_port = source_port;
		getUINetObject().addLine(source_port, this);
		getUINetObject().setDraggingPort(source_port, this);
	}

	public void removeTempPort() {
		getUINetObject().removeLine(source_port, this);
		getUINetObject().setDraggingPort(null, null);
	}

	private void moveTempPort(Point position) { // Relative coordinates
		translation.x = position.x - original.x;
		translation.y = position.y - original.y;
	}

	@Override
	protected void onEvent(IEvent event) {
		mouse.handle(event);
	}

	@Override
	public void onDraw() {
		UINodeUtil.getWindow(this).debug.set("UIPortTemporary " + this, String.format("source_port=%s, target=%s", source_port == null ? null : source_port.name, target == null ? null : target.name));
	}

	private void tryConnect() {
		UIPort source_port = getUINetObject().getDraggingSourcePort();
		UIPortTemporary temp_port = getUINetObject().getTemporaryPort();
		UIPort target_port = temp_port.target;

		if(target_port != null) {
			if(target_port == source_port) return;
			if(target_port.getParent() == source_port.getParent()) return;

			// If we are an input port, clear any line that is already connected to us (should only be 1 line)
			try {
				getUINetObject().connect(source_port, target_port);
			} catch (NetList.AlreadyConnected e) {
				// Okido
			}
		}
	}
}
