package net.merayen.elastic.ui.objects.components.curvebox

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.MutablePoint
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.logWarning
import net.merayen.elastic.util.math.BezierCurve
import net.merayen.elastic.util.math.SignalBezierCurve
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// TODO make more generic, UI-wise
class BezierCurveBox : UIObject(), BezierCurveBoxInterface {
	var layoutWidth = 100f
	var layoutHeight = 100f

	/**
	 * Use this object to draw stuff inside our view.
	 * NOTE: It uses a relative 0 to 1 scale!
	 */
	val background = UIObject()

	private val points = ArrayList<BezierDot>()
	var handler: Handler? = null

	val bezierPoints: Array<BezierDot>
		get() = points.toTypedArray()

	val pointCount: Int
		get() = points.size

	val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	val contextMenuItemAdd = TextContextMenuItem("Add")

	/**
	 * Gets all points as a flat list of floats in this format: [p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, ...]
	 * @return
	 */
	val floats: List<Float>
		get() {
			val result = ArrayList<Float>()

			for (bd in points) {
				result.add(bd.left_dot.translation.x)
				result.add(bd.left_dot.translation.y)

				result.add(bd.position.translation.x)
				result.add(bd.position.translation.y)

				result.add(bd.right_dot.translation.x)
				result.add(bd.right_dot.translation.y)
			}

			return result
		}

	interface Handler {
		/**
		 * Called every time a dot moves a pixel.
		 */
		fun onMove(point: BezierDot)

		/**
		 * Called after user has let go of a dot.
		 */
		fun onChange()

		/**
		 * When user clicks
		 */
		fun onSelect(dot: BezierDotDragable)

		/**
		 * User wants to add a new point at position x and y
		 */
		fun onAdd(x: Float, y: Float)

		/**
		 * User wants to remove a point
		 *
		 * Return true if point should be removed.
		 */
		fun onRemove(point: BezierDot)
	}

	inner class BezierDot : UIObject() {
		var position = BezierDotDragable(this, false)
		var left_dot = BezierDotDragable(this, true)
		var right_dot = BezierDotDragable(this, true)

		init {
			add(position)
			add(left_dot)
			add(right_dot)

			left_dot.radius = 0.02f
			right_dot.radius = 0.02f

			left_dot.color.red = 1f
			left_dot.color.green = 1f
			left_dot.color.blue = 0.8f

			right_dot.color.red = 1f
			right_dot.color.green = 1f
			right_dot.color.blue = 0.8f
		}
	}

	inner class BezierDotDragable internal constructor(
		private val point: BezierDot,
		private val isHandle: Boolean
	) : UIObject() {
		val color = MutableColor(255, 200, 0)
		var radius = 0.05f

		lateinit var movable: Movable
		var visible = true // Set to false to hide the dot (not possible to move it)

		val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)

		private val contextMenuItemRemove = TextContextMenuItem("Remove")

		override fun onInit() {
			val self = this

			movable = Movable(this, this, MouseEvent.Button.LEFT)
			movable.setHandler(object : Movable.IMoveable {
				override fun onMove() {
					translation.x = max(0f, min(1f, translation.x))
					translation.y = max(0f, min(1f, translation.y))
					handler?.onMove(point)
				}

				override fun onGrab() {
					handler?.onSelect(self)
				}

				override fun onDrop() {
					handler?.onChange()
				}
			})

			if (!isHandle) {
				for (i in 0 until 4)
					contextMenu.addMenuItem(EmptyContextMenuItem())

				contextMenu.addMenuItem(contextMenuItemRemove)
				contextMenu.handler = object : ContextMenu.Handler {
					override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
						if (item == contextMenuItemRemove) {
							handler?.onRemove(point)
						}
					}

					override fun onMouseDown(position: MutablePoint) {}
				}
			}
		}

		override fun onDraw(draw: Draw) {
			if (visible) {
				val rY = radius * (layoutWidth / layoutHeight).pow(0.5f)
				val rX = radius * (layoutHeight / layoutWidth).pow(0.5f)
				draw.setColor(color.red, color.green, color.blue)
				draw.fillOval(-rX / 2, -rY / 2, rX, rY)
			}
		}

		override fun onEvent(event: UIEvent) {
			if (visible) {
				movable.handle(event)
				contextMenu.handle(event)
			}
		}
	}

	init {
		initPoints()
	}

	override fun onInit() {
		add(background, 0)

		contextMenu.addMenuItem(contextMenuItemAdd)
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.handler = object : ContextMenu.Handler {
			override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
				when (item) {
					// Figure out where to insert the point
					contextMenuItemAdd -> {
						handler?.onAdd(position.x, position.y)
					}
				}
			}

			override fun onMouseDown(position: MutablePoint) {}
		}
	}

	private fun initPoints() {  // FIXME This should perhaps not be here? Could allow whatever bezier...?
		val start = BezierDot()
		start.position.translation.y = 0.5f
		start.right_dot.translation.x = 0.5f
		start.right_dot.translation.y = 0.75f
		start.left_dot.visible = false // Not being used
		points.add(start)
		add(start)

		val stop = BezierDot()
		stop.position.translation.x = 1f
		stop.position.translation.y = 0.5f
		stop.left_dot.translation.x = 0.5f
		stop.left_dot.translation.y = 0.25f
		stop.right_dot.visible = false // Not being used

		points.add(stop)
		add(stop)
	}

	override fun onDraw(draw: Draw) {
		// Make our content scale to a 0 to 1 coordinate system
		translation.scaleX = 1 / layoutWidth
		translation.scaleY = 1 / layoutHeight
		translation.clip = Rect(0f, 0f, layoutWidth + 1, layoutHeight + 1)

		//draw.setColor(20, 20, 40)
		//draw.fillRect(0f, 0f, 1f, 1f)

		draw.setColor(.3f, .3f, .3f)
		draw.setStroke(1 / ((layoutWidth + layoutHeight) / 2))
		draw.rect(0f, 0f, 1f, 1f)

		// The curve
		draw.setColor(255, 200, 0)
		val p = arrayOfNulls<MutablePoint>((points.size - 1) * 3)

		var i = 0
		for (j in 1 until points.size) {
			val before = points[j - 1]
			val current = points[j]

			p[i++] = MutablePoint(before.right_dot.translation.x, before.right_dot.translation.y)
			p[i++] = MutablePoint(current.left_dot.translation.x, current.left_dot.translation.y)
			p[i++] = MutablePoint(current.position.translation.x, current.position.translation.y)
		}

		val bps = points[0] // The initial point
		@Suppress("UNCHECKED_CAST")
		draw.bezier(bps.position.translation.x, bps.position.translation.y, p as Array<Point>)

		// Draw lines from the dots to the points
		drawDotLines(draw)

		//drawDiagnostics();
	}

	override fun onEvent(event: UIEvent) {
		contextMenu.handle(event)
	}

	private fun drawDotLines(draw: Draw) {
		draw.setStroke(1 / (layoutWidth + layoutHeight))
		draw.setColor(200, 180, 0)

		for (bp in points) {
			if (bp.left_dot.visible)
				draw.line(
					bp.position.translation.x,
					bp.position.translation.y,
					bp.left_dot.translation.x,
					bp.left_dot.translation.y
				)

			if (bp.right_dot.visible)
				draw.line(bp.position.translation.x, bp.position.translation.y, bp.right_dot.translation.x, bp.right_dot.translation.y)
		}
	}

	override fun insertPoint(before_index: Int): BezierDot {
		if (before_index < 1)  // FIXME Maybe not here, as we should support circular beziers
			throw RuntimeException("Can not insert before the first point as it is fixed")

		if (before_index > points.size - 1)  // FIXME Maybe not here, as we should support circular beziers
			throw RuntimeException("Can not add after the last point as it is fixed")

		val bpa = BezierDot()

		points.add(before_index, bpa)
		add(bpa)

		return bpa
	}

	fun removePoint(index: Int) {
		val point = points.removeAt(index)
		point.parent?.remove(point)
	}

	fun removePoint(point: BezierDot) {
		val index = points.indexOf(point)
		if (index > -1) {
			removePoint(index)
		} else logWarning("Could not find point")
	}

	fun getBezierPoint(index: Int): BezierDot {
		return points[index]
	}

	fun getIndex(point: BezierDot): Int {
		return points.indexOf(point)
	}

	fun setPoints(new_points: List<Number>) {
		if (new_points.size % 6 != 0)
			throw RuntimeException("Invalid point length")

		clearPoints()

		var i = 0
		while (i < new_points.size) {
			val dot = BezierDot()
			dot.left_dot.translation.x = new_points[i++].toFloat()
			dot.left_dot.translation.y = new_points[i++].toFloat()
			dot.position.translation.x = new_points[i++].toFloat()
			dot.position.translation.y = new_points[i++].toFloat()
			dot.right_dot.translation.x = new_points[i++].toFloat()
			dot.right_dot.translation.y = new_points[i++].toFloat()

			points.add(dot)
			add(dot)
		}
	}

	private fun clearPoints() {
		for (o in points)
			remove(o)

		points.clear()
	}

	private fun drawDiagnostics(draw: Draw) {
		val dots = BezierCurve.fromFlat(floats)
		val result = FloatArray(1000)
		SignalBezierCurve.getValues(dots, result)

		draw.setColor(0, 255, 255)
		for (i in result.indices)
			draw.fillOval(i / result.size.toFloat() - 0.005f, result[i] - 0.005f, 0.01f, 0.01f)
	}
}
