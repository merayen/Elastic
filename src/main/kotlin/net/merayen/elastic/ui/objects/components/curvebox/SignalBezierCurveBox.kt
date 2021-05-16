package net.merayen.elastic.ui.objects.components.curvebox

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox.BezierDot
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox.BezierDotDragable
import net.merayen.elastic.util.MutablePoint
import net.merayen.elastic.util.math.BezierCurve
import net.merayen.elastic.util.math.SignalBezierCurve
import kotlin.math.min

/**
 * Bezier curve for shaping signals.
 * It limits how the curve can be managed.
 */
class SignalBezierCurveBox : UIObject(), BezierCurveBoxInterface { // Move out from test_100

	var layoutWidth = 100f
	var layoutHeight = 100f

	private val curve = BezierCurveBox()
	private var offset: Float = 0.toFloat()
	private var handler: Handler? = null

	private var moving: Boolean = false // Don't accept new points when user is interacting with us

	private val dots: Array<BezierCurve.Dot>
		get() {
			val points = curve.bezierPoints
			val result = arrayOfNulls<BezierCurve.Dot>(points.size)

			for (i in points.indices) {
				val bp = points[i]
				result[i] = BezierCurve.Dot(
						MutablePoint(bp.position.translation.x, bp.position.translation.y),
						MutablePoint(bp.left_dot.translation.x, bp.left_dot.translation.y),
						MutablePoint(bp.right_dot.translation.x, bp.right_dot.translation.y)
				)
			}

			@Suppress("UNCHECKED_CAST")
			return result as Array<BezierCurve.Dot>
		}

	val floats: List<Float>
		get() = curve.floats

	interface Handler {
		/**
		 * Called when user has changed the curve.
		 */
		fun onChange()

		/**
		 * Called very often when user is changing something.
		 */
		fun onMove()

		/**
		 * When user clicks down on a dot.
		 */
		fun onDotClick()
	}

	private inner class Overlay : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setColor(50, 50, 0)
			//draw.setStroke(min(curve.layoutWidth / curve.layoutHeight, curve.layoutHeight / curve.layoutWidth) * 0.02f)
			draw.line(0f, offset, 1f, offset)
		}
	}

	init {
		// We do not allow user moving the start and stop point
		curve.getBezierPoint(0).position.visible = false
		curve.getBezierPoint(1).position.visible = false
	}

	override fun onInit() {
		curve.handler = object : BezierCurveBox.Handler {
			override fun onMove(point: BezierDot) {
				moving = true

				constrainPoint(point)
				offset = getOffset()

				if (handler != null)
					handler!!.onMove()
			}

			override fun onChange() {
				moving = false
				if (handler != null)
					handler!!.onChange()
			}

			override fun onSelect(dot: BezierDotDragable) {
				dot.color.red = 0.15f
			}
		}

		add(curve)

		curve.background.add(Overlay())

		offset = getOffset()
	}

	fun setHandler(handler: Handler) {
		this.handler = handler
	}

	override fun insertPoint(index: Int): BezierDot {
		val before = curve.getBezierPoint(index - 1)
		val after = curve.getBezierPoint(index)

		val bpa = curve.insertPoint(1)

		// Come up with a position for the new point XXX fix, this sucks
		bpa.position.translation.x = (before.position.translation.x + after.position.translation.x) / 2
		bpa.position.translation.y = (before.position.translation.y + after.position.translation.y) / 2

		bpa.right_dot.translation.x = bpa.position.translation.x + 0.04f
		bpa.right_dot.translation.y = bpa.position.translation.y + 0.04f

		bpa.left_dot.translation.x = bpa.position.translation.x - 0.04f
		bpa.left_dot.translation.y = bpa.position.translation.y - 0.04f

		constrainAllPoints()

		return bpa
	}

	/**
	 * Makes sure all points are valid. Do this if you have manually edited any points.
	 * This ensures that the bezier doesn't go back in X-axis.
	 */
	private fun constrainAllPoints() {
		for (bp in curve.bezierPoints)
			constrainPoint(bp)
	}

	private fun constrainPoint(point: BezierDot) {
		val index = curve.getIndex(point)

		// Constrain X-axis for both handles on the point, to the points around
		if (index > 0) {
			val before = curve.getBezierPoint(index - 1)
			if (point.left_dot.translation.x < before.position.translation.x)
				point.left_dot.translation.x = before.position.translation.x

			if (point.position.translation.x < before.position.translation.x)
				point.position.translation.x = before.position.translation.x

			if (point.position.translation.x < before.right_dot.translation.x)
				point.position.translation.x = before.right_dot.translation.x
		}

		if (index < curve.pointCount - 1) {
			val after = curve.getBezierPoint(index + 1)

			if (point.right_dot.translation.x > after.position.translation.x)
				point.right_dot.translation.x = after.position.translation.x

			if (point.position.translation.x > after.position.translation.x)
				point.position.translation.x = after.position.translation.x

			if (point.position.translation.x > after.left_dot.translation.x)
				point.position.translation.x = after.left_dot.translation.x
		}

		if (point.right_dot.translation.x < point.position.translation.x)
			point.right_dot.translation.x = point.position.translation.x

		if (point.left_dot.translation.x > point.position.translation.x)
			point.left_dot.translation.x = point.position.translation.x
	}

	override fun onUpdate() {
		curve.layoutWidth = layoutWidth
		curve.layoutHeight = layoutHeight
	}

	fun getOffset(): Float {
		val data = FloatArray(OFFSET_LINE_RESOLUTION)
		SignalBezierCurve.getValues(dots, data)

		var result = 0.0
		for (f in data)
			result += f.toDouble()

		return (result / OFFSET_LINE_RESOLUTION).toFloat()
	}

	fun setPoints(new_points: List<Float>) {
		if (moving)
			return  // Don't accept new points when user is interacting with us

		curve.setPoints(new_points)

		val start = curve.getBezierPoint(0)
		start.left_dot.visible = false
		start.position.visible = false
		start.position.translation.x = 0f
		start.position.translation.y = 0.5f

		val stop = curve.getBezierPoint(curve.pointCount - 1)
		stop.position.visible = false
		stop.right_dot.visible = false
		stop.position.translation.x = 1f
		stop.position.translation.y = 0.5f

		offset = getOffset()
	}

	companion object {

		private const val OFFSET_LINE_RESOLUTION = 100
	}
}
