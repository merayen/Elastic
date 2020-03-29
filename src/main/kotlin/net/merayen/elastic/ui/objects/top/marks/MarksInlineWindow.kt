package net.merayen.elastic.ui.objects.top.marks

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

/**
 * Shows an inline window with a list of marks.
 * TODO This should be somewhere else, be very general.
 * We do want to set marks everywhere, like on a node or on some note in a piano roll.
 */
class MarksInlineWindow(private val mode: Mode) : UIObject(), EasyMotionBranch {
	enum class Mode {
		SET,
		GOTO,
		DELETE
	}

	interface Handler {
		fun onClose()

		/**
		 * Mark has gotten selected.
		 */
		fun onSelect(mark: Char)
	}

	var handler: Handler? = null

	private val window = InlineWindow()

	override val easyMotionBranch = object : Branch(this, window) {
		init {
			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				this@MarksInlineWindow.handler?.onClose()
				null
			}

			controls[setOf()] = Control {
				if (it.modifiers.isEmpty() || (it.modifiers.size == 1 && it.modifiers[0].key == KeyboardEvent.Keys.SHIFT)) {
					val character = when {
						CharRange('a', 'z').contains(it.character) -> it.character!!
						CharRange('A', 'Z').contains(it.character) -> it.character!!
						else -> null
					}

					if (character != null) {
						this@MarksInlineWindow.handler?.onSelect(character)
						this@MarksInlineWindow.handler?.onClose()
					}
				}
				null
			}
		}
	}

	override fun onInit() {
		window.title = when (mode) {
			Mode.SET -> "Set mark"
			Mode.GOTO -> "Goto mark"
			Mode.DELETE -> "Remove mark"
		}

		buttonGenerator('a', 26, 0f)
		buttonGenerator('0', 10, 100f)
		buttonGenerator('A', 26, 140f)
		add(window)
	}

	private fun buttonGenerator(start: Char, length: Int, yOffset: Float) {
		val maxWidthCount = 10
		var char = start
		for (i in 0 until length) {
			val button = Button()
			button.label = char.toString()
			char++
			button.translation.x = ((i % maxWidthCount) * 30f)
			button.translation.y = yOffset + 30f * (i / maxWidthCount)
			window.content.add(button)
		}
	}
}