package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.controller.Gate;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.util.DrawContext;
import net.merayen.elastic.util.Postmaster;

/**
 * This runs in the main-thread (???), and represents everything UI, at least locally.
 * (Remote UI might be an option later on)
 */
public class Supervisor {
	public interface Handler {
		/**
		 * Called by the UI thread. DO NOT DO ANY TIME-CONSUMING STUFF IN THIS CALL.
		 * It will hang the UI.
		 * Rather, queue the message and notify() whatever needs to react on the message.
		 */
		public void onMessageToBackend(Postmaster.Message message);

		/**
		 * Called by the UI-thread when ready to receive message.
		 * Call Supervisor().sendMessageToUI(...) in a loop until you have no messages.
		 */
		public void onReadyForMessages();
	}

	private final Handler handler;
	SurfaceHandler surfacehandler;
	private final Top top;
	private final Gate.UIGate ui_gate; // Only to be used by the UI-thread
	private final Gate.BackendGate backend_gate; // Only to be used by the main-thread
	private final Gate gate;

	public Supervisor(Handler handler) {
		this.handler = handler;

		top = new Top();

		Supervisor self = this;
		gate = new Gate(top, new Gate.Handler() {
			@Override
			public void onMessageToBackend(Postmaster.Message message) {
				self.handler.onMessageToBackend(message);
			}
		});
		ui_gate = gate.getUIGate();
		top.setUIGate(ui_gate);
		backend_gate = gate.getBackendGate();

		initSurface();
	}

	void draw(DrawContext dc) {
		internalDraw(dc, top);
		internalUpdate(dc.incoming_events); // TODO maybe do it in another thread ?
		internalExecuteIncomingMessages();
		ui_gate.update();
	}

	/**
	 * Drawing of UIObjects. Only allowed to draw, not adding/removing UIObjects etc.
	 */
	private void internalDraw(DrawContext dc, UIObject uiobject) {
		dc.push(uiobject);

		uiobject.draw_z = dc.pushZIndex();
		uiobject.absolute_translation = dc.translation_stack.getAbsolute();

		if(!uiobject.isInitialized())
			uiobject.initialize();

		if(uiobject.absolute_translation.visible) {
			Draw draw = new Draw(uiobject, dc);

			uiobject.updateDraw(draw);

			uiobject.outline_abs_px = draw.getAbsoluteOutline();

			draw.destroy();

			for(UIObject o : new ArrayList<>(uiobject.children))
				internalDraw(dc, o);

		} else {
			uiobject.outline_abs_px = new Rect();
		}

		dc.pop();
	}

	/**
	 * Non-draw update of UIObjects.
	 * Here the UIObjects can change their properties, add/remove UIObjects, etc.
	 */
	private void internalUpdate(List<IEvent> events) {
		List<UIObject> list = top.search.getAllChildren();
		list.add(0, top);

		for(UIObject o : list) {
			if(!(o instanceof Top) && !o.isAttached()) // UIObject is not connected to the tree. Most likely detached under an previously onUpdate(), after getAllChildren() was called
				continue;

			if(o.isInitialized()) // UIObject probably created in a previous onInit(), and has not been initialized yet, if this skips
				for(IEvent e : events)
					o.onEvent(e);

			o.onUpdate();
		}
	}

	private void internalExecuteIncomingMessages() {
		handler.onReadyForMessages();
	}

	public void sendMessageToUI(Postmaster.Message message) {
		backend_gate.send(message);
	}

	public void end() {
		surfacehandler.end();
	}

	public JSONObject dump() {
		return gate.dump();
	}

	private void initSurface() {
		surfacehandler = new SurfaceHandler(this);
	}
}
