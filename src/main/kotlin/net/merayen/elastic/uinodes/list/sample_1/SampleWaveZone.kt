package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint
import kotlin.math.max
import kotlin.math.min

class SampleWaveZone(private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onSelect()
		fun onChange()
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var start = 0f
		set(value) {
			field = min(1f - (stop - start), max(0f, value))
		}

	var stop = 0f
		set(value) {
			field = min(1f, max(start, value))
		}

	var focus = false

	private val mouse = MouseHandler(this, MouseEvent.Button.LEFT)
	private var dragStart: Float? = null
	private var dragStop = 0f

	override fun onInit() {
		mouse.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: MutablePoint?) {
				handler.onSelect()

				dragStart = start
				dragStop = stop
			}

			override fun onMouseDrag(position: MutablePoint?, offset: MutablePoint?) {
				val dragStart = dragStart

				if(dragStart != null) {
					start = dragStart + offset!!.x / layoutWidth
					stop = start + (dragStop - dragStart)
					handler.onChange()
				}
			}
		})
	}

	override fun onDraw(draw: Draw) {
		val x = start * layoutWidth
		val width = stop * layoutWidth - start * layoutWidth

		draw.setColor(1f, 1f, 1f, 0.5f)
		draw.fillRect(x, 0f, width, layoutHeight)

		if(focus) {
			draw.setColor(1f, 1f, 0f, 0.5f)
			draw.setStroke(1f)
			draw.rect(x, 0f, width, layoutHeight)
		}
	}

	override fun onEvent(event: UIEvent) {
		mouse.handle(event)
	}
}