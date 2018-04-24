package net.merayen.elastic.ui.controller

import net.merayen.elastic.ui.intercom.EditNodeMessage
import net.merayen.elastic.ui.intercom.ViewportHelloMessage
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView
import net.merayen.elastic.util.Postmaster.Message

class ViewportController(gate: Gate) : Controller(gate) {
	private var viewportContainer: ViewportContainer? = null

	override fun onInit() {}

	override fun onMessageFromBackend(message: Message) {}

	override fun onMessageFromUI(message: Message) {
		if (message is ViewportHelloMessage) // Received from ViewportContainer UIObject when it has inited. We can then manage it
			viewportContainer = message.viewport_container
	}
}
