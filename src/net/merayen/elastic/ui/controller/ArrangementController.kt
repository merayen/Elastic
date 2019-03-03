package net.merayen.elastic.ui.controller

import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView
import net.merayen.elastic.util.Postmaster

class ArrangementController internal constructor(gate: Gate): Controller(gate) {
	/**
	 * ArrangementView send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : Postmaster.Message()

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {
		for (view in getViews(ArrangementView::class.java)) {
			view.handleMessage(message)
		}
	}

	override fun onMessageFromUI(message: Postmaster.Message) {}
}