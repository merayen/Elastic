package net.merayen.elastic.ui.controller;

import java.util.ArrayList;
import java.util.List;

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

			// Sending of messages to UI
			if(gate.top.isInitialized()) // Require Top to be initialized
				while((message = gate.from_backend.receive()) != null)
					for(Controller c : gate.list)
						c.onMessageFromBackend(message);

			// Sending of messages to backend
			while((message = gate.from_ui.receive()) != null)
				for(Controller c : gate.list)
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

		public Postmaster.Message receive() {
			return gate.to_backend.receive();
		}
	}

	List<Controller> list = new ArrayList<>();

	final Top top;

	private final UIGate ui_gate;
	private final BackendGate backend_gate;

	private final Postmaster from_backend = new Postmaster(); // Incoming from backend
	//private final Postmaster to_ui = new Postmaster(); // Processed by the controllers, but since Controller acts directly with UIObjects, we have no buffer 
	private final Postmaster from_ui = new Postmaster(); // Messages sent from UI awaiting to be processed by a Controller
	final Postmaster to_backend = new Postmaster(); // Processed by Controller, awaiting to be read by backend

	public Gate(Top top) {
		this.top = top;
		ui_gate = new UIGate(this);
		backend_gate = new BackendGate(this);

		list.add(new NodeController(this));
		list.add(new NetController(this));
	}

	public UIGate getUIGate() {
		return ui_gate;
	}

	public BackendGate getBackendGate() {
		return backend_gate;
	}
}
