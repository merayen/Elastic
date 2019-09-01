package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.intercom.ViewportHelloMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer

class ViewportController(top: Top) : Controller(top) {
	private var viewportContainer: ViewportContainer? = null

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {}

	override fun onMessageFromUI(message: ElasticMessage) {
		if (message is ViewportHelloMessage) // Received from ViewportContainer UIObject when it has inited. We can then manage it
			viewportContainer = message.viewport_container
	}
}
