package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.objects.top.Top

class NetListController(top: Top) : Controller(top) {
	override fun onInit() {}
	override fun onMessageFromBackend(message: ElasticMessage) {}
	override fun onMessageFromUI(message: ElasticMessage) {}
}
