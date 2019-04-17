package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.Point

abstract class BaseTimeLine : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * User is now sizing a selection.
		 * Receiver of this event should probably process and allow the selection to span over several tracks.
		 */
		fun onSelectionDrag(start: Point, offset: Point)

		/**
		 * User lets go of the selection.
		 */
		fun onSelectionDrop(start: Point, offset: Point)
	}
}