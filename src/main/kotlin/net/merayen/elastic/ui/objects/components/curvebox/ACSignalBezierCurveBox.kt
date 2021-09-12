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
		val point = curve.insertPoint(before_index)
		constrainPoints()
		return point
	}

	fun setPoints(new_points: List<Float>) {
		curve.setPoints(new_points)
		constrainPoints()
	}

	private fun constrainPoints() {
		val first = curve.getBezierPoint(0)
		val last = curve.getBezierPoint(curve.pointCount - 1)

		first.position.visible = false
		first.position.translation.x = 0f
		first.position.translation.y = 0.5f

		last.position.visible = false
		last.position.translation.x = 1f
		last.position.translation.y = 0.5f
	}
}