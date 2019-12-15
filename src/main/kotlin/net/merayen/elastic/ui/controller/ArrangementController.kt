package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView

class ArrangementController internal constructor(top: Top) : Controller(top) {
	/**
	 * ArrangementView send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : ElasticMessage

	private val arrangementViews: List<ArrangementView>
	get() {
		return getViews(ArrangementView::class.java).map { it.arrangementViewController = this; it }.toList()
	}

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		for (view in getViews(ArrangementView::class.java))
			view.handleMessage(message)
	}

	override fun onMessageFromUI(message: ElasticMessage) {
		if (message is Hello)
			arrangementViews
	}
}