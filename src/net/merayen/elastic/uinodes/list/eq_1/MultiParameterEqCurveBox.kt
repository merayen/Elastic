package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point
import kotlin.math.pow

class MultiParameterEqCurveBox(private val eqPoints: MultiParameterEqData, private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onCreatePoint()
		fun onDeletePoint()
		fun onChangePoint()
	}

	private inner class Dot : UIObject() {
		val mouseHandler = MouseHandler(this, MouseEvent.Button.LEFT)

		override fun onInit() {
			mouseHandler.setHandler(object : MouseHandler.Handler() {
				override fun onMouseDown(position: Point?) {
					createPoint(position!!.x)
				}
			})
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0.8f, 0.8f, 0f)
			draw.fillOval(0f, 0f, 5f, 5f)
		}

		override fun onEvent(event: UIEvent) {

		}
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val MAX_FREQUENCY = 22000f
	private val FREQUENCY_STEPS = 10

	private val mouseHandler = MouseHandler(this)

	private val dots = ArrayList<Dot>()

	private var horizontalLine: Float? = null

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: Point?) {
				handler.onCreatePoint()
			}

			override fun onMouseMove(point: Point) {
				horizontalLine = point.x
			}

			override fun onMouseOut() {
				horizontalLine = null
			}
		})
	}

	override fun onDraw(draw: Draw) {
		// The background
		draw.setColor(0, 0, 0)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		// Vertical line guides
		draw.setFont("", 5f)
		draw.setStroke(1f)
		for (i in 0 until FREQUENCY_STEPS) {
			val frequency = ((i / FREQUENCY_STEPS.toFloat()).pow(2f) * MAX_FREQUENCY)

			val linePos = (i / FREQUENCY_STEPS.toFloat()) * layoutWidth

			draw.setColor(0.3f, 0.2f, 0.3f)
			draw.line(linePos, 0f, linePos, layoutHeight)

			draw.setColor(0.6f, 0.6f, 0.6f)
			draw.text("${(frequency).toInt()} Hz", linePos + 1, layoutHeight - 5)
		}

		// Position line that follows mouse
		val horizontalLine = horizontalLine
		if(horizontalLine != null) {
			draw.setColor(1f, 1f, 1f)
			draw.setStroke(1f)
			draw.line(horizontalLine, 0f, horizontalLine, layoutHeight)
		}

		// The EQ-curve
		draw.setColor(1f, 1f, 0f)
		draw.setStroke(1f)
		draw.bezier(0f, layoutHeight / 2, arrayOf<Point>(Point(layoutWidth, 0f), Point(0f, layoutHeight), Point(layoutWidth, layoutHeight / 2)))

		// Border
		draw.setStroke(1f)
		draw.setColor(50, 50, 50)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}

	private fun createPoint(x: Float) {
		val dot = Dot()
		dot.translation.x = x
		dot.translation.y = layoutHeight / 2
		add(dot)
		dots.add(dot)
	}
}
