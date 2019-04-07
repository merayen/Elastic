package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import net.merayen.elastic.system.intercom.FinishResetNetListMessage
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import java.util.ArrayList

class ArrangementController internal constructor(gate: Gate) : Controller(gate) {
	/**
	 * ArrangementView send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : Postmaster.Message()

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {
		for (view in getViews(ArrangementView::class.java))
			view.handleMessage(message)
	}

	override fun onMessageFromUI(message: Postmaster.Message) {
		if (message is NetListRefreshRequestMessage) { // Replay whole NetList for the arrangement view
			val messages = ArrayList<Postmaster.Message>()
			messages.add(BeginResetNetListMessage())
			messages.addAll(NetListMessages.disassemble(gate.netlist))
			messages.add(FinishResetNetListMessage())

			for (m in messages)
				onMessageFromBackend(m)
		}
	}
}