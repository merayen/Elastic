package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.util.Movable
import kotlin.math.max

class TrackPane : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 50f

	val buttons = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox())

	private val resizer = object : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setStroke(4f)
			draw.setColor(0.2f, 0.2f, 0.2f)
			draw.fillRect(0f, 0f, layoutWidth, 4f)
		}
	}

	private val movable = Movable(resizer, resizer, MouseEvent.Button.LEFT)

	init {
		movable.setHandler(object : Movable.IMoveable {
			override fun onGrab() {}

			override fun onMove() {
				layoutHeight = max(resizer.translation.y + 2f, 50f)
				resizer.translation.x = 0f
			}

			override fun onDrop() {}

		})
		add(resizer)

		buttons.translation.x = 5f
		buttons.translation.y = 5f
		add(buttons)
	}

	override fun onUpdate() {
		resizer.translation.y = layoutHeight - 2f
	}

	override fun onEvent(event: UIEvent) {
		movable.handle(event)
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}