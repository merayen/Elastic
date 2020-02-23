package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.DirectTextInput
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.TextInputBox
import net.merayen.elastic.ui.objects.components.listbox.GridListBox
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState
import kotlin.random.Random

class AddNodeWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()
	}

	var handler: Handler? = null

	private val window = InlineWindow()
	private val textInput = TextInputBox()
	private val resultListBox = GridListBox()

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
		textInput.directTextInput.color.red = 1f
		textInput.directTextInput.color.green = 1f
		textInput.directTextInput.color.blue = 1f
		window.content.add(textInput)

		window.handler = object : InlineWindow.Handler {
			override fun onClose() {
				handler?.onClose()
			}
		}

		textInput.directTextInput.handler = object : DirectTextInput.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke): Boolean {
				if (keyStroke.hasKey(KeyboardEvent.Keys.ENTER))
					return false // Suppress multiline

				return true
			}

			override fun onChange() {
				resultListBox.items.removeAll()
				for (i in 0 until Random.nextInt(2, 10)) {
					resultListBox.items.add(Label("Node nummer $i", eventTransparent = false))
				}
			}
		}

		textInput.layoutWidth = 200f
		textInput.layoutHeight = 15f

		resultListBox.translation.x = 2f
		resultListBox.translation.y = 27f
		resultListBox.layoutWidth = 100f
		resultListBox.layoutHeight = 100f
		window.content.add(resultListBox)
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
				this@AddNodeWindow.handler?.onClose()
				null
			}
		}
	}
}