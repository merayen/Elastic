package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.util.Movable
import kotlin.math.max

/**
 * A floating window drawn inside view.
 */
class InlineWindow : UIObject() {
	interface Handler {
		fun onClose()
	}

	var title = ""
	var margin = 5f
	val content = UIObject()

	var handler: Handler? = null

	private var calculatedWidth = 0f
	private var calculatedHeight = 0f

	private val titleBar = object : UIObject() {
		private val close = Button()

		override fun onInit() {
			close.translation.y = 2f
			close.label = "x"
			close.handler = object : Button.IHandler {
				override fun onClick() {
					handler?.onClose()
				}
			}
			add(close)
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0.5f, 0.5f, 0.5f)
			draw.fillRect(0f, 0f, calculatedWidth, 20f)

			draw.setColor(0f, 0f, 0f)
			draw.setFont("", 10f)
			draw.text(title, margin, margin + 10f)
		}

		override fun onUpdate() {
			close.translation.x = calculatedWidth - 17f
		}
	}

	private val movable = Movable(this, titleBar, MouseEvent.Button.LEFT)

	override fun onInit() {
		content.translation.x = margin
		content.translation.y = margin + 20f
		add(content)

		add(titleBar)
	}

	override fun onDraw(draw: Draw) {
		calculatedWidth = max(50f, content.getWidth() + margin * 2)
		calculatedHeight = max(50f, content.getHeight() + 20f + margin * 2)

		draw.setColor(0.1f, 0.1f, 0.1f)
		draw.fillRect(0f, 0f, calculatedWidth, calculatedHeight)

		draw.setColor(0.7f, 0.7f, 0.7f)
		draw.setStroke(2f)
		draw.line(0f, 0f, calculatedWidth, 0f)
		draw.line(calculatedWidth, 0f, calculatedWidth, calculatedHeight)
		draw.line(calculatedWidth, calculatedHeight, 0f, calculatedHeight)
		draw.line(0f, calculatedHeight, 0f, 0f)
	}

	override fun onEvent(event: UIEvent) {
		movable.handle(event)
	}
}