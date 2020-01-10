package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.util.Pacer
import kotlin.math.max
import kotlin.math.min

class Meter : UIObject(), FlexibleDimension {
	enum class Direction { UP, RIGHT, DOWN, LEFT }
	inner class Content : UIClip() {
		var text = ""
		var textColor = MutableColor(0.8f, 0.8f, 0.8f)

		override fun onDraw(draw: Draw) {
			super.onDraw(draw)
			if (!text.isBlank()) {
				draw.setColor(textColor)
				val fontSize = min(layoutWidth, layoutHeight) / 2
				draw.setFont("", fontSize)
				val textWidth = draw.getTextWidth(text)
				draw.text(text, layoutWidth / 2 - textWidth / 2, layoutHeight / 2 + fontSize / 2)
			}
		}
	}

	override var layoutWidth = 100f
	override var layoutHeight = 20f

	val content = Content()

	var direction = Direction.RIGHT

	var speed = 10f

	private val pacer = Pacer()

	var color = MutableColor(0.2f, 0.8f, 0.2f)
	var value = 0f
		set(value) {
			field = max(0f, min(1f, value))
		}

	private var currentValue = 0f

	override fun onInit() {
		add(content)
	}

	override fun onDraw(draw: Draw) {
		pacer.update()

		currentValue += (value - currentValue) * pacer.getDiff(speed)

		draw.setStroke(1f)

		// Background
		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		// Meter
		draw.setColor(color)

		when (direction) {
			Direction.RIGHT -> {
				draw.fillRect(
						2f,
						2f,
						currentValue * (layoutWidth - 4),
						layoutHeight - 4
				)
			}
			Direction.DOWN -> {
				draw.fillRect(
						2f,
						2f,
						layoutWidth - 2,
						currentValue * (layoutHeight - 4)
				)
			}
			Direction.LEFT -> {
				draw.fillRect(
						2f + (layoutWidth - 4f) * (1 - currentValue),
						2f,
						layoutWidth - 2f - (layoutWidth - 4f) * (1 - currentValue),
						layoutHeight - 4f
				)
			}
			Direction.UP -> {
				draw.fillRect(
						2f,
						(layoutHeight - 4f) * (1 - currentValue),
						layoutWidth - 4f,
						layoutHeight - 2f - (layoutHeight - 4f) * (1 - currentValue)
				)
			}
		}

		// Frame
		draw.setColor(0.1f, 0.1f, 0.1f)
		draw.rect(0f, 0f, layoutWidth - 1, layoutHeight - 1)
	}

	override fun onUpdate() {
		content.layoutWidth = layoutWidth
		content.layoutHeight = layoutHeight
	}
}