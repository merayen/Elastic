package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

/**
 * Text input using EasyMotion. Probably to replace TextInput soon.
 */
class DirectTextInput : UIObject(), FlexibleDimension, EasyMotionBranch {
	override var layoutWidth = 50f
	override var layoutHeight = 50f;

	var value = ""

	private val mouseHandler = MouseHandler(this)

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseClick(position: MutablePoint?) {
				easyMotionBranch.focus()
			}
		})
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setStroke(1f)
		draw.setColor(0.8f, 0.8f, 0.8f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0.3f, 0.3f, 0.3f)
		draw.rect(0f, 0f, layoutWidth - 1, layoutHeight - 1)

		draw.setColor(0, 0, 0)
		draw.setFont("", 10f)
		draw.text(value, 2f, layoutHeight - 4)
	}

	override val easyMotionBranch = object : Branch(this) {
		init {
			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				Control.STEP_BACK
			}

			controls[setOf()] = Control {  keys -> // This should receive any keys
				println("DirectTextInput: I have received any-key now, $keys")
				null
			}
		}
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}