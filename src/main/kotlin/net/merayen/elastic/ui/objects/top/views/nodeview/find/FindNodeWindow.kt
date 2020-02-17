package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.DirectTextInput
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState

class FindNodeWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()
	}

	var handler: Handler? = null

	private val window = InlineWindow()
	private val textInput = DirectTextInput()

	override fun onInit() {
		window.title = "Find node"
		add(window)

		window.content.add(object : UIObject() {
			override fun onDraw(draw: Draw) {
				draw.setColor(1f, 0f, 1f)
				draw.setStroke(2f)
				draw.empty(0f, 0f, 100f, 100f)
			}
		})

		textInput.translation.x = 2f
		textInput.translation.y = 2f
		textInput.color.red = 1f
		textInput.color.green = 1f
		textInput.color.blue = 1f
		window.content.add(textInput)

		window.handler = object : InlineWindow.Handler {
			override fun onClose() {
				handler?.onClose()
			}
		}

		textInput.handler = object : DirectTextInput.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke): Boolean {
				if (keyStroke.hasKey(KeyboardEvent.Keys.ENTER))
					return false // Suppress multiline

				return true
			}
		}
	}

	private var focused = false

	override fun onUpdate() {
		if (!focused) {
			textInput.focus()
			focused = true
		}
	}

	override val easyMotionBranch = object : Branch(this) {
		init {
			controls[setOf(KeyboardEvent.Keys.Q)] = Control {
				this@FindNodeWindow.handler?.onClose()
				null
			}
		}
	}
}