package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.framework.PopupParameter

/**
 * Not fully implemented yet (no use for it yet)
 */
class TimePopupParameter : UIObject() {
	var seconds = 0f
	set(value) {
		if (value != field) {
			field = value
			val hours = seconds / 3600
			val minutes = (seconds / 60) % 60
			val seconds = seconds % 60
			val milliseconds = (seconds * 100) % 100
			valueLabel.text = "$hours:$minutes:$seconds.$milliseconds"
		}
	}

	val label = Label()
	private val valueLabel = Label()

	private val minified = object : UIObject() {
		override fun onInit() {
			valueLabel.translation.x = 5f
			valueLabel.translation.y = 5f
			valueLabel.text = "00:00:00.00"
			add(valueLabel)
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0f, 0f, 0f)
			draw.fillRect(0f, 0f, valueLabel.labelWidth + 10f, valueLabel.fontSize + 10f)
		}
	}

	private val popup = object : UIObject() {
		override fun onDraw(draw: Draw) {
			//draw.setColor()
		}
	}

	private val popupParameter = PopupParameter(minified, popup)

	override fun onInit() {
		popupParameter.setHandler(object : PopupParameter.Handler {
			override fun onGrab() {}
			override fun onMove() {
				println(popupParameter.y)
			}
			override fun onDrop() {}
		})

		add(popupParameter)

		seconds = 0f
	}
}