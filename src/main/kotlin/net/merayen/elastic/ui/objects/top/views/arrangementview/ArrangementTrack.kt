package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.UIObject
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

	abstract fun onParameter(instance: BaseNodeData)

	/**
	 * Called when selection rectangle has changed.
	 * @param selectionRectangle Use this object to get its position and size (via getRelativePosition(selectionRectangle))
	 */
	abstract fun onSelectionRectangle(selectionRectangle: UIObject)

	abstract fun clearSelections()

	fun sendParameter(instance: BaseNodeData) = arrangement.sendMessage(NodeParameterMessage(nodeId, instance))
}
