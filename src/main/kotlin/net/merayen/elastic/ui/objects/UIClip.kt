package net.merayen.elastic.ui.objects

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject

/**
 * Draws children inside our self, clipping by our size.
 * TODO implement caching?
 */
open class UIClip : UIObject(), FlexibleDimension {
	override var layoutWidth = 50f
	override var layoutHeight = 50f

	override fun onDraw(draw: Draw) {
		translation.clip = Rect(0f, 0f, layoutWidth, layoutHeight)
	}

	/**
	 * Returns true if the object is visible inside us.
	 * Does not check if it is really a child of us.
	 * The UIObject must implement the FlexibleDimension interface.
	 */
	fun isVisible(uiobject: UIObject): Boolean {
		uiobject as FlexibleDimension

		val relative = getRelativePosition(uiobject)!!

		return relative.x + uiobject.layoutWidth > 0 && relative.y + uiobject.layoutHeight > 0 &&
			relative.x < layoutWidth && relative.y < layoutHeight
	}
}
