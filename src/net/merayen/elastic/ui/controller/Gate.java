package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.util.Postmaster;

/**
 * A controller communicates with the backend, and manages the nodes in the UI.
 */
public class Gate {
	/**
	 * Only used by the UI-thread.
	 */
	public static class UIGate {
		private Gate gate;

		private UIGate(Gate gate) {
			this.gate = gate;
		}

		/**
		 * Send message to backend, via the Controllers
		 */
		public void send(Postmaster.Message message) {
			gate.from_ui.send(message);
		}

		/**
		 * Call this often from the UI thread to handle incoming and outgoing messages.
		 */
		public void update() {
			Postmaster.Message message;

			if(!gate.top.isInitialized())
				return;

			if(!gate.inited)
				gate.init();

			// Sending of messages to UI
			while((message = gate.from_backend.receive()) != null)
				for(Controller c : gate.controllers)
					c.onMessageFromBackend(message);

			// Sending of messages to backend
			while((message = gate.from_ui.receive()) != null)
				for(Controller c : gate.controllers)
					c.onMessageFromUI(message);
		}
	}

	/**
	 * Only used by the backend-thread.
	 */
	public static class BackendGate {
		private Gate gate;

		private BackendGate(Gate gate) {
			this.gate = gate;
		}

		/**
		 * Send message to UI, via the Controllers
		 */
		public void send(Postmaster.Message message) {
			gate.from_backend.send(message);
		}
	}

	public interface Handler {
		public void onMessageToBackend(Postmaster.Message message);
	}

	private final Handler handler;
	private boolean inited;
	List<Controller> controllers = new ArrayList<>();
	private JSONObject to_load;

	final Top top;

	private final UIGate ui_gate;
	private final BackendGate backend_gate;

	private final Postmaster from_backend = new Postmaster(); // Incoming from backend
	//private final Postmaster to_ui = new Postmaster(); // Processed by the controllers, but since Controller acts directly with UIObjects, we have no buffer 
	private final Postmaster from_ui = new Postmaster(); // Messages sent from UI awaiting to be processed by a Controller
	//final Postmaster to_backend = new Postmaster(); // Processed by Controller, awaiting to be read by backend

	public Gate(Top top, Handler handler) {
		this.top = top;
		this.handler = handler;
		ui_gate = new UIGate(this);
		backend_gate = new BackendGate(this);

		controllers.add(new NetListController(this));
		controllers.add(new ViewportController(this));
		controllers.add(new NodeViewController(this));
	}

	public UIGate getUIGate() {
		return ui_gate;
	}

	public BackendGate getBackendGate() {
		return backend_gate;
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		if(!inited)
			throw new RuntimeException("Gate() has not inited yet. Too early to dump");

		JSONObject result = new JSONObject();
		JSONObject ctrls = new JSONObject();
		result.put("controllers", ctrls);

		for(Controller c : controllers) {
			JSONObject obj = c.onDump();
			if(obj != null)
				ctrls.put(c.getClass().getSimpleName(), obj);
		}

		return result;
	}

	public void restore(JSONObject obj) {
		if(inited)
			throw new RuntimeException("Restoring state must be done before calling any update() on UIGate, also before initied");

		to_load = obj;
	}

	private void init() {
		for(Controller c : controllers)
			c.onInit();

		if(to_load != null) {
			for(Controller c : controllers) {
				JSONObject data = (JSONObject)to_load.get(c.getClass().getSimpleName());
				c.onRestore(data);
			}
		}

		inited = true;
	}

	void sendMessageToBackend(Postmaster.Message message) {
		handler.onMessageToBackend(message);
	}
}
