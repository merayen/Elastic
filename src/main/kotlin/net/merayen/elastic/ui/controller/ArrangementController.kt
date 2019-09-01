package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.FinishResetNetListMessage
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView
import net.merayen.elastic.util.NetListMessages
import java.util.*

class ArrangementController internal constructor(top: Top) : Controller(top) {
	/**
	 * ArrangementView send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : ElasticMessage

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		for (view in getViews(ArrangementView::class.java))
			view.handleMessage(message)
	}

	override fun onMessageFromUI(message: ElasticMessage) {
		if (message is NetListRefreshRequestMessage) { // Replay whole NetList for the arrangement view
			val messages = ArrayList<ElasticMessage>()
			messages.add(BeginResetNetListMessage())
			messages.addAll(NetListMessages.disassemble(top.netlist))
			messages.add(FinishResetNetListMessage())

			for (m in messages)
				onMessageFromBackend(m)
		}
	}
}