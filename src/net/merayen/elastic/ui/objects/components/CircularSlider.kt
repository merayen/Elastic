package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class CircularSlider : UIObject() {
	var size = 30f
	var dragScale = 0.25f
	var handler: Handler? = null

	val label = Label()
	val valueLabel = Label()

	// In radian, min and max position
	var min = Math.PI.toFloat() * 1.8f
	var max = Math.PI.toFloat() * 0.2f

	var value = 0f
		set(value) {
			if(value != field) {
				field = Math.min(Math.max(value, 0f), 1f)
				valueLabel.text = handler?.onLabelUpdate(field) ?: ""
				println("Value change ${field} ${this.value} ${handler}")
			}
		}

	lateinit var mousehandler: MouseHandler
	private var dragValue: Float = 0.toFloat()

	interface Handler {
		fun onChange(value: Float)
		fun onLabelUpdate(value: Float) = ""
	}

	override fun onInit() {
		val mousehandler = MouseHandler(this)
		mousehandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDrag(start_point: Point, offset: Point) {
				val prevValue = value
				value = dragValue - offset.y / (size / dragScale)
				if(value != prevValue) {
					handler?.onChange(value)
					valueLabel.text = handler?.onLabelUpdate(value) ?: ""
				}
			}

			override fun onMouseDown(position: Point) {
				dragValue = value
			}
		})

		this.mousehandler = mousehandler

		label.align = Label.Align.CENTER
		label.eventTransparent = true
		add(label)

		valueLabel.align = Label.Align.CENTER
		valueLabel.eventTransparent = true
		add(valueLabel)
	}

	override fun onDraw(draw: Draw) {
		draw.setStroke(1.5f)
		draw.setColor(30, 30, 30)
		draw.oval(0f, 0f, size, size)

		draw.setColor(30, 30, 30)
		drawLine(draw, 0f, 0.8f)
		drawLine(draw, 1f, 0.8f)

		draw.setColor(200, 200, 200)
		drawLine(draw, value, 0.6f)
	}

	override fun onUpdate() {
		label.translation.x = size / 2
		label.translation.y = size
		label.fontSize = size / 4

		valueLabel.translation.x = size / 2
		valueLabel.translation.y = size / 3
		valueLabel.fontSize = size / 5
	}

	override fun onEvent(e: UIEvent) {
		mousehandler.handle(e)
	}

	private fun drawLine(draw: Draw, value: Float, length: Float) {
		//value = Math.max(Math.min(value, 1f), 0f);
		draw.line(
				size / 2 + Math.sin((min + value * (max - min)).toDouble()).toFloat() * (size * length) / 2.1f,
				size / 2 + Math.cos((min + value * (max - min)).toDouble()).toFloat() * (size * length) / 2.1f,
				size / 2 + Math.sin((min + value * (max - min)).toDouble()).toFloat() * size / 2.3f,
				size / 2 + Math.cos((min + value * (max - min)).toDouble()).toFloat() * size / 2.3f
		)
	}
}
