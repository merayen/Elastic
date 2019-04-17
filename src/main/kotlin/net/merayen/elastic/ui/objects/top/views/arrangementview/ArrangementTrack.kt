package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.util.Point

abstract class ArrangementTrack(val nodeId: String, private val arrangement: Arrangement) {
	interface Handler {
		/**
		 * E.g when user clicks outside, deselect everything in arrangement view
		 */
		fun onEventSelect()

		fun onSelectionDrag(start: Point, offset: Point)
		fun onSelectionDrop(start: Point, offset: Point)
	}
	val trackPane = TrackPane()
	val eventPane = EventPane()

	abstract fun onParameter(key: String, value: Any)

	abstract fun clearSelections()

	fun sendParameter(key: String, value: Any) = arrangement.sendMessage(NodeParameterMessage(nodeId, key, value))
}
