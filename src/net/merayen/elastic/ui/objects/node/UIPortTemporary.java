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
	private UIPort destination_port;

	public UIPortTemporary(UINode uinode, UIPort source_port, UIPort destination_port) {
		super("temporary_dragging_port", false, uinode);
		this.source_port = source_port;
		this.destination_port = destination_port;
		draw_default_port = false;

		translation.x = 0;
		translation.y = 0;
		source_port.add(this);

		getUINetObject().addLine(source_port, this);
		getUINetObject().setDraggingPort(source_port, this);

		mouse = new MouseHandler(this, Button.LEFT);
		mouse.setHandler(new MouseHandler.Handler() {
			@Override
			public void onGlobalMouseUp(Point global_position) {
				tryConnect();
				removeTempPort();
			}

			@Override
			public void onGlobalMouseMove(Point position) {
				moveTempPort(source_port.getRelativeFromAbsolute(position.x, position.y));
			}
		});
	}

	private void removeTempPort() {
		getUINetObject().removeLine(source_port, this);
		getUINetObject().setDraggingPort(null, null);
		getParent().remove(this);
	}

	private void moveTempPort(Point position) { // Relative coordinates
		translation.x = position.x;// - original.x;
		translation.y = position.y;// - original.y;
	}

	@Override
	protected void onEvent(IEvent event) {
		mouse.handle(event);
	}

	@Override
	protected void onDraw() {
		UINodeUtil.getWindow(this).debug.set("UIPortTemporary " + this, String.format("source_port=%s, target=%s", source_port == null ? null : source_port.name, target == null ? null : target.name));
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		// Sets the initial offset
		if(destination_port != null) {
			Point pos = getRelativePosition(destination_port);
			translation.x = pos.x;
			translation.y = pos.y;
			destination_port = null;
		}
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
