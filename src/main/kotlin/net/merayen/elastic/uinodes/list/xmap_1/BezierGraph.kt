package net.merayen.elastic.uinodes.list.xmap_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox

/**
 * Shows a view with a bezier curve with some tools at the top.
 */
class BezierGraph : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 80f

	private val toolbar = BezierGraphToolbar()
	private val bezier = BezierCurveBox()

	override fun onInit() {
		toolbar.layoutHeight = 20f
		add(toolbar)

		bezier.translation.y = 20f
		add(bezier)
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.5f, 0.5f, 0.5f) // TODO remove when ever
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		toolbar.layoutWidth = layoutWidth
		bezier.layoutWidth = layoutWidth
		bezier.layoutHeight = layoutHeight - 20f
	}
}