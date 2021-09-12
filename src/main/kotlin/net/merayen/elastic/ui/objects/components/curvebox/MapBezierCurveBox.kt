package net.merayen.elastic.ui.objects.components.curvebox

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

/**
 * Presents a bezier inside a box that can be manipulated inside
 * the box.
 */
class MapBezierCurveBox : UIObject(), BezierCurveBoxInterface, FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val curve = ForwardBezierCurveBox()

	override fun onInit() {
		curve.setHandler(object : ForwardBezierCurveBox.Handler {
			override fun onChange() {}
			override fun onMove() {}
			override fun onDotClick() {}
		})

		add(curve)
	}

	override fun insertPoint(before_index: Int): BezierCurveBox.BezierDot {
		return curve.insertPoint(before_index)
	}

	override fun onUpdate() {
		curve.layoutWidth = layoutWidth
		curve.layoutHeight = layoutHeight
	}
}