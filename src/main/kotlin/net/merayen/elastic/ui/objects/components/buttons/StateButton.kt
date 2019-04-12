package net.merayen.elastic.ui.objects.components.buttons

import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.UIObject

open class StateButton : UIObject() {
	interface Handler {
		fun onClick(value: Boolean)
	}

	private val button = Button()

	var label = ""
		set(value) {
			field = value
			button.label = value
		}

	var handler: Handler? = null

	var textColor = Color(255, 255, 255)
	var backgroundColor = Color(100, 100, 100)

	var value = false
		set(value) {
			field = value
			updateButtonColor()
		}

	override fun onInit() {
		button.handler = object : Button.IHandler {
			override fun onClick() {
				value = value xor true
				handler?.onClick(value)
			}

		}
		add(button)
		updateButtonColor()
	}

	private fun updateButtonColor() {
		button.textColor = if (value) textColor else Color(textColor.red / 2, textColor.green / 2, textColor.blue / 2)
		button.backgroundColor = if (value) backgroundColor else Color(backgroundColor.red / 2, backgroundColor.green / 2, backgroundColor.blue / 2)
	}
}