package net.merayen.elastic.ui.objects.top.views.nodeview.inlinewindows

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

/**
 * Shows an inline window with a list of marks.
 */
class MarksInlineWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()

		/**
		 * Mark has gotten selected.
		 */
		fun onSelect(mark: Char)
	}

	private class Content : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.text("Du er teit", 20f, 20f)
		}
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
				if (it.modifiers.isEmpty()) { // TODO support big letters too, for global marks
					this@MarksInlineWindow.handler?.onSelect(it.keys.first().character)
					this@MarksInlineWindow.handler?.onClose()
				}
				null
			}
		}
	}

	override fun onInit() {
		add(window)
	}
}