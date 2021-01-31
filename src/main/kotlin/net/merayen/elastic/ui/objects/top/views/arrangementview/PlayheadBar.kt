package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint
import kotlin.math.roundToInt

class PlayheadBar : UIObject(), FlexibleDimension {
	interface Handler {
		fun onSelectionChange()
	}

	override var layoutWidth = 100f
	override var layoutHeight = 20f

	var beatWidth = 10f
	var division = 4

	private var selectionPosition1 = 0f
	private var selectionPosition2 = 0f

	/**
	 * Selection range in beats.
	 */
	var selectionRange: Pair<Float, Float>
		get() {
			return when {
				selectionPosition1 < selectionPosition2 -> Pair(selectionPosition1, selectionPosition2)
				selectionPosition1 > selectionPosition2 -> Pair(selectionPosition2, selectionPosition1)
				else -> Pair(0f, 0f)
			}
		}
		set(value) {
			if (mouseHandler.isDown)
				return

			if (value != null) {
				selectionPosition1 = value.first
				selectionPosition2 = value.second
			} else {
				selectionPosition1 = 0f
				selectionPosition2 = 0f
			}
		}

	var handler: Handler? = null

	private val mouseHandler = MouseHandler(this)

	override fun onInit() {
		mouseHandler.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: MutablePoint) {
				selectionPosition1 = (position.x / beatWidth).roundToInt().toFloat()
				selectionPosition2 = (position.x / beatWidth).roundToInt().toFloat()
				handler?.onSelectionChange()
			}

			override fun onMouseDrag(position: MutablePoint, offset: MutablePoint) {
				val before = selectionRange
				selectionPosition2 = (position.x / beatWidth).roundToInt().toFloat()
				if (selectionRange != before)
					handler?.onSelectionChange()
			}
		})
	}

	override fun onDraw(draw: Draw) { // TODO only draw what is visible in the clip
		val visibility = visibility ?: return
		visibility.clip(0f, 0f, Float.MAX_VALUE, layoutHeight)

		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.fillRect(visibility.x1, 0f, visibility.width, layoutHeight)

		val selectionRange = selectionRange
		if (selectionRange != null) {
			draw.setColor(0.7f, 0.6f, 0f)
			val selectionRect = Rect(selectionRange.first * beatWidth, 2f, selectionRange.second * beatWidth, layoutHeight - 4)
			selectionRect.clip(visibility)
			draw.fillRect(selectionRect.x1, selectionRect.y1, selectionRect.width, selectionRect.height)
		}

		// Position labels
		var pos = (visibility.x1 / beatWidth).toInt() * beatWidth
		while (pos < visibility.x2) {
			val bar = (pos / beatWidth / division).toInt()
			val beat = ((pos / beatWidth) % division).toInt()

			if (beat == 0) {
				draw.setFont("", 12f)
				draw.setColor(1f, 1f, 1f)
				draw.text(bar.toString(), pos - draw.getTextWidth(bar.toString()) / 2, 16f)
			} else {
				draw.setFont("", 9f)
				draw.setColor(.8f, .8f, .8f)
				draw.text(beat.toString(), pos - draw.getTextWidth(bar.toString()) / 2, 9f)
			}
			pos += beatWidth
		}
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}