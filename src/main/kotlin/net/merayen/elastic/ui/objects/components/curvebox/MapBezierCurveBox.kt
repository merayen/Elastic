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

	interface Handler {
		fun onChange()
		fun onMove()
	}

	var handler: Handler? = null

	val floats: List<Float>
		get() = curve.floats

	override fun onInit() {
		curve.setHandler(object : ForwardBezierCurveBox.Handler {
			override fun onChange() {
				handler?.onChange()
			}
			override fun onMove() {
				handler?.onMove()
			}
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

	fun setPoints(points: List<Float>) {
		curve.setPoints(points)

		val first = curve.getBezierPoint(0)
		val last = curve.getBezierPoint(curve.pointCount - 1)
	}
}