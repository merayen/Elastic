package net.merayen.elastic.ui.controller

import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import java.util.ArrayList

import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster

/**
 * A controller communicates with the backend, and manages the nodes in the UI.
 */
class Gate(val top: Top, private val handler: Handler) {
	private var inited: Boolean = false
	val netlist = NetList()
	internal var controllers: MutableList<Controller> = ArrayList()

	val uiGate: UIGate
	val backendGate: BackendGate

	private val fromBackend = Postmaster() // Incoming from backend
	private val fromUI = Postmaster() // Messages sent from UI awaiting to be processed by a Controller

	/**
	 * Only used by the UI-thread.
	 */
	inner class UIGate {

		/**
		 * Send message to backend, via the Controllers
		 */
		fun send(message: Postmaster.Message) {
			fromUI.send(message)
		}

		/**
		 * Call this often from the UI thread to handle incoming and outgoing messages.
		 */
		fun update() {
			if (!top.isInitialized)
				return

			if (!inited)
				init()

			// Sending of messages to UI
			while(!fromBackend.isEmpty) {
				val message = fromBackend.receive()

				if(message is BeginResetNetListMessage)
					netlist.clear()

				NetListMessages.apply(netlist, message)

				for (c in controllers)
					c.onMessageFromBackend(message)

			}

			// Sending of messages to backend
			while(!fromUI.isEmpty) {
				val message = fromUI.receive()

				for (c in controllers)
					c.onMessageFromUI(message)

			}
		}
	}

	/**
	 * Only used by the backend-thread.
	 */
	inner class BackendGate {

		/**
		 * Send message to UI, via the Controllers
		 */
		fun send(message: Postmaster.Message) {
			fromBackend.send(message)
		}
	}

	interface Handler {
		fun onMessageToBackend(message: Postmaster.Message)
	}

	init {
		uiGate = UIGate()
		backendGate = BackendGate()

		controllers.add(NetListController(this))
		controllers.add(ViewportController(this))
		controllers.add(NodeViewController(this))
		controllers.add(EditNodeController(this))
		controllers.add(NativeUIController(this))
	}

	private fun init() {
		for (c in controllers)
			c.onInit()

		inited = true
	}

	fun sendMessageToBackend(message: Postmaster.Message) {
		handler.onMessageToBackend(message)
	}

	fun runPostDrawJobs() {
		for(controller in controllers)
			controller.onAfterDraw()
	}
}
