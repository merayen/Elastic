package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.DirectTextInput
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.TextInputBox
import net.merayen.elastic.ui.objects.components.listbox.ListBox
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState
import net.merayen.elastic.uinodes.UINodeInformation

class AddNodeWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()
	}

	var handler: Handler? = null

	private val window = InlineWindow()
	private val textInput = TextInputBox()
	private val resultListBox = ListBox()

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
				val text = textInput.directTextInput.text.toLowerCase()

				val nodeInfos = UINodeInformation.getNodeInfos()
				resultListBox.list.removeAll()

				nodeInfos.filter {
					text in it.name.toLowerCase() || text in it.description.toLowerCase()
				}

				nodeInfos.sortBy {
					val name = it.name.toLowerCase()
					if (name.startsWith(text))
						name
					else
						"\uFFFF${name}"
				}

				for (nodeInfo in nodeInfos)
					if (text in nodeInfo.name.toLowerCase() || text in nodeInfo.description.toLowerCase())
						resultListBox.list.add(Label(nodeInfo.name, eventTransparent = false))
			}
		}

		textInput.layoutWidth = 200f
		textInput.layoutHeight = 15f

		resultListBox.translation.x = 2f
		resultListBox.translation.y = 27f
		resultListBox.layoutWidth = 200f
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