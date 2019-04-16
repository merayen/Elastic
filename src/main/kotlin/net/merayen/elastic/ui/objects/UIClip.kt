package net.merayen.elastic.ui.objects

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject

/**
 * Draws children inside ourself, clipping by our size.
 * TODO implement caching?
 */
open class UIClip : UIObject(), FlexibleDimension {
	override var layoutWidth = 50f
	override var layoutHeight = 50f

	override fun onDraw(draw: Draw) {
		translation.clip = Rect(0f, 0f, layoutWidth, layoutHeight)
	}
}
