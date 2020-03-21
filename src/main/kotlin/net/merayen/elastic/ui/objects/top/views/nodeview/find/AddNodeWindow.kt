package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.TextInput
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.TextInputBox
import net.merayen.elastic.ui.objects.components.listbox.ListBox
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.uinodes.UINodeInformation

class AddNodeWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()
		fun onSelect(node: BaseInfo)
	}

	var handler: Handler? = null

	private val window = InlineWindow()
	private val textInputBox = TextInputBox()
	private val resultListBox = ListBox()

	override fun onInit() {
		window.title = "Add node"
		add(window)

		window.content.add(object : UIObject() {
			override fun onDraw(draw: Draw) {
				draw.setColor(1f, 0f, 1f)
				draw.setStroke(2f)
				draw.empty(0f, 0f, 100f, 100f)
			}
		})

		textInputBox.translation.x = 2f
		textInputBox.translation.y = 2f
		textInputBox.textInput.color.red = 1f
		textInputBox.textInput.color.green = 1f
		textInputBox.textInput.color.blue = 1f
		window.content.add(textInputBox)

		window.handler = object : InlineWindow.Handler {
			override fun onClose() {
				handler?.onClose()
			}
		}

		textInputBox.textInput.handler = object : TextInput.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke): Boolean {
				when {
					keyStroke.hasKey(KeyboardEvent.Keys.ENTER) -> select()
					keyStroke.hasKey(KeyboardEvent.Keys.DOWN) -> println("user wannwa go down on result")
					keyStroke.hasKey(KeyboardEvent.Keys.UP) -> println("oh god, now the user wannwa go down on result")
					else -> return true
				}

				return false
			}

			override fun onChange() {
				search(textInputBox.textInput.text)
			}
		}

		textInputBox.layoutWidth = 200f
		textInputBox.layoutHeight = 15f

		resultListBox.translation.x = 2f
		resultListBox.translation.y = 27f
		resultListBox.layoutWidth = 200f
		resultListBox.layoutHeight = 100f
		window.content.add(resultListBox)

		search("")
	}

	private var focused = false

	override fun onUpdate() {
		if (!focused) {
			textInputBox.focus()
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

	private fun search(text: String) {
		val text = text.toLowerCase()

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

	private fun select() {

	}
}