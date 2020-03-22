package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.listbox.ListBox
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState
import kotlin.math.max
import kotlin.math.min

/**
 * Generic window that takes character input and searches for values.
 */
class FilterInlineWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		/**
		 * User leaves the window, without selecting an item.
		 */
		fun onClose()

		/**
		 * User types something.
		 * You should then update the search results.
		 */
		fun onSearch(text: String)

		/**
		 * User clicks on or pushes enter on an item.
		 */
		fun onSelect(uiobject: UIObject)
	}

	var handler: Handler? = null

	var title = ""

	private val window = InlineWindow()
	private val textInputBox = TextInputBox()
	private val resultListBox = ListBox()
	var selected: UIObject? = null

	override val easyMotionBranch = object : Branch(this, window) {
		init {
			controls[setOf(KeyboardEvent.Keys.Q)] = Control {
				this@FilterInlineWindow.handler?.onClose()
				null
			}

			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				this@FilterInlineWindow.handler?.onClose()
				null
			}
		}
	}

	override fun onInit() {
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
					keyStroke.hasKey(KeyboardEvent.Keys.DOWN) -> move(1)
					keyStroke.hasKey(KeyboardEvent.Keys.UP) -> move(-1)
					else -> return true
				}

				return false
			}

			override fun onChange() {
				handler?.onSearch(textInputBox.textInput.text)
			}
		}

		textInputBox.layoutWidth = 200f
		textInputBox.layoutHeight = 15f

		resultListBox.translation.x = 2f
		resultListBox.translation.y = 27f
		resultListBox.layoutWidth = 200f
		resultListBox.layoutHeight = 100f
		window.content.add(resultListBox)

		handler?.onSearch("")
	}

	private var focused = false

	override fun onUpdate() {
		window.title = title

		if (!focused) {
			textInputBox.focus()
			focused = true
		}
	}

	/**
	 * Call this method to which items to show.
	 */
	fun setResults(list: List<UIObject>) {
		if (selected !in list)
			selected = null

		resultListBox.list.removeAll()

		for (uiobject in list)
			resultListBox.list.add(uiobject)
	}

	private fun select() {
		handler?.onSelect(selected ?: return)
	}

	private fun move(direction: Int) {
		val list = resultListBox.list
		val children = list.children

		if (children.isEmpty())
			return

		val selected = selected

		this.selected = when (direction) {
			-1 -> {
				if (selected == null)
					children[children.size - 1]
				else
					children[max(children.indexOf(selected) - 1, 0)]
			}
			1 -> {
				if (selected == null)
					children[0]
				else
					children[min(children.indexOf(selected) + 1, children.size - 1)]
			}
			else -> throw RuntimeException("Should not happen")
		}

		resultListBox.selections.clear()
		resultListBox.selections.add(this.selected!!)
	}
}
