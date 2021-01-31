package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView

class ArrangementController internal constructor(top: Top) : Controller(top) {
	/**
	 * ArrangementView send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : ElasticMessage

	class PlayheadPositionChange(val beat: Float) : ElasticMessage
	class RangeSelectionChange(val range: Pair<Float, Float>) : ElasticMessage

	var topNodeId: String? = null
		private set // The topmost node. Automatically figured out upon restoration.

	private val arrangementViews: List<ArrangementView>
		get() {
			return getViews(ArrangementView::class.java).map { it.arrangementViewController = this; it }.toList()
		}

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		if (message is CreateNodeMessage && topNodeId == null)
			topNodeId = message.nodeId

		for (view in getViews(ArrangementView::class.java))
			view.handleMessage(message)
	}

	override fun onMessageFromUI(message: ElasticMessage) {
		when (message) {
			is Hello -> arrangementViews
			is PlayheadPositionChange -> {
				val topNodeId = topNodeId ?: return
					sendToBackend(NodePropertyMessage(
						topNodeId,
						net.merayen.elastic.backend.logicnodes.list.group_1.Properties(playheadPosition = message.beat)
					))
			}
			is RangeSelectionChange -> {
				val topNodeId = topNodeId ?: return
				sendToBackend(NodePropertyMessage(
						topNodeId,
						net.merayen.elastic.backend.logicnodes.list.group_1.Properties(
								rangeSelectionStart = message.range?.first ?: 0f,
								rangeSelectionStop = message.range?.second ?: 0f
						)
				))
			}
		}
	}
}