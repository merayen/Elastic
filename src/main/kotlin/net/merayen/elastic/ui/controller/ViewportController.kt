package net.merayen.elastic.ui.controller

import net.merayen.elastic.ui.intercom.ViewportHelloMessage
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer

class ViewportController(gate: Gate) : Controller(gate) {
	private var viewportContainer: ViewportContainer? = null

	override fun onInit() {}

	override fun onMessageFromBackend(message: Any) {}

	override fun onMessageFromUI(message: Any) {
		if (message is ViewportHelloMessage) // Received from ViewportContainer UIObject when it has inited. We can then manage it
			viewportContainer = message.viewport_container
	}
}
