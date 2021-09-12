package net.merayen.elastic.ui.objects.components.curvebox

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

/**
 * Bezier curve for AC signals, removes any DC
 */
class ACSignalBezierCurveBox : UIObject(), BezierCurveBoxInterface, FlexibleDimension {
	interface Handler {
		fun onMove()
		fun onChange()
		fun onDotClick()
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f
	private val curve = ForwardBezierCurveBox()

	val floats: List<Float>
		get() = curve.floats

	var handler: Handler? = null

	init {
		curve.setHandler(object : ForwardBezierCurveBox.Handler {
			override fun onChange() {
				handler?.onChange()
			}

			override fun onMove() {
				handler?.onMove()
			}

			override fun onDotClick() {
				handler?.onDotClick()
			}
		})
	}

	override fun onInit() {
		add(curve)
	}

	override fun onUpdate() {
		curve.layoutWidth = layoutWidth
		curve.layoutHeight = layoutHeight
	}

	override fun insertPoint(before_index: Int): BezierCurveBox.BezierDot {
		return curve.insertPoint(before_index)
	}

	fun setPoints(new_points: List<Float>) = curve.setPoints(new_points)
}