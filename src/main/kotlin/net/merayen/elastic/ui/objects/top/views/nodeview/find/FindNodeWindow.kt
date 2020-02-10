package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.InlineWindow
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

class FindNodeWindow : UIObject(), EasyMotionBranch {
	interface Handler {
		fun onClose()
	}

	var handler: Handler? = null

	private val window = InlineWindow()

	init {
		window.title = "Find node"
		add(window)

		window.content.add(object : UIObject() {
			override fun onDraw(draw: Draw) {
				draw.setColor(1f, 0f, 1f)
				draw.setStroke(2f)
				draw.oval(0f, 0f, 100f, 100f)
			}
		})
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