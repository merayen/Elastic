package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.MutableColor
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint
import kotlin.math.max
import kotlin.math.min

/**
 * Text input using EasyMotion. Probably to replace TextInput soon.
 */
class DirectTextInput : UIObject(), EasyMotionBranch {
	class LineSelectionRange(val start: Int, val stop: Int)

	inner class SelectionRange(
		val startX: Int,
		val startY: Int,
		val stopX: Int,
		val stopY: Int
	) {
		init {
			if (startX < 0 || startY < 0 || startY > stopY)
				throw RuntimeException("Should not happen")

			if (startY == stopY && startX > stopX)
				throw RuntimeException("Should not happen")
		}

		/**
		 * Get the range of the selection on a single line.
		 */
		fun getLineRange(y: Int) = when (y) {
			startY -> LineSelectionRange(startX, if (y == stopY) stopX else lines[y].length) // First line
			stopY -> LineSelectionRange(0, stopX) // Last line
			else -> LineSelectionRange(0, lines[y].length) // One of the middle lines
		}
	}

	var maxWidth = 50f

	var fontSize = 10f
	val color = MutableColor()
	var lineSpace = 2f

	private val lines = ArrayList<String>()
	private var cursorPositionX = 0
	private var cursorPositionY = 0

	private var marking = false // Not implemented yet
	private var markerBeginX = 0
	private var markerBeginY = 0

	private val mouseHandler = MouseHandler(this)

	private val keyStrokeQueue = ArrayList<KeyboardState.KeyStroke>()

	init {
		lines.add("")
	}

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

		draw.setColor(0.2f, 0.2f, 0.2f)
		draw.fillRect(0f, 0f, maxWidth, lines.size * (fontSize + lineSpace))

		draw.setFont("", 10f)

		val selectionRange = getSelectionRange()

		for ((y, line) in lines.withIndex()) {
			if (marking) {

				if (y in min(cursorPositionY, markerBeginY)..max(cursorPositionY, markerBeginY)) {
					val lineSelectionRectangle = selectionRange.getLineRange(y)

					val x = draw.getTextWidth(lines[y].substring(0, lineSelectionRectangle.start))
					val width = draw.getTextWidth(lines[y].substring(lineSelectionRectangle.start, lineSelectionRectangle.stop))

					draw.setColor(1f, 0f, 1f, 0.5f)
					draw.fillRect(x, 1 + y * (fontSize + lineSpace), width, 1 + fontSize + lineSpace)
				}
			}

			draw.setColor(color)
			draw.text(line, 2f, fontSize + y * (fontSize + lineSpace))

			if (cursorPositionY == y && easyMotionBranch.inFocus) {
				if ((System.currentTimeMillis() / 1000L) % 2 == 0L) {
					val textWidthBeforeMarker = draw.getTextWidth(line.substring(0, cursorPositionX))
					draw.line(textWidthBeforeMarker + 1, 1 + y * (fontSize + lineSpace), textWidthBeforeMarker + 1, 1 + y * (fontSize + lineSpace) + fontSize + lineSpace)
				}
			}
		}

		handleKeyStrokeQueue(draw)
	}

	override val easyMotionBranch = object : Branch(this) {
		init {
			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				Control.STEP_BACK
			}

			controls[setOf()] = Control { keys ->
				keyStrokeQueue.add(keys)
				null
			}
		}
	}

	private fun handleKeyStrokeQueue(draw: Draw) {
		for (keyStroke in keyStrokeQueue) {
			if (keyStroke.hasKey(KeyboardEvent.Keys.CONTROL) || keyStroke.hasKey(KeyboardEvent.Keys.ALT))
				continue

			// This should receive any keys
			if (keyStroke.equalsKeys(setOf(KeyboardEvent.Keys.BACKSPACE))) {
				if (marking) {
					removeText(getSelectionRange())
				} else {
					if (cursorPositionX > 0) {
						cursorPositionX--

						val text = lines[cursorPositionY]
						lines[cursorPositionY] = text.substring(0, cursorPositionX) + text.substring(cursorPositionX + 1, text.length)
					} else if (cursorPositionY > 0) {
						val targetLineLengthBeforeMerging = lines[cursorPositionY - 1].length

						lines[cursorPositionY - 1] += lines[cursorPositionY]
						lines.removeAt(cursorPositionY)

						cursorPositionX = targetLineLengthBeforeMerging
						cursorPositionY--
					}
				}

			} else if (keyStroke.equalsKeys(setOf(KeyboardEvent.Keys.ENTER))) {
				if (marking)
					removeText(getSelectionRange())

				val bringOverText = if (cursorPositionX < lines[cursorPositionY].length) // TODO Not working!
					lines[cursorPositionY].substring(cursorPositionX)
				else
					""

				lines.add(cursorPositionY + 1, bringOverText)

				if (bringOverText.isNotEmpty())
					lines[cursorPositionY] = lines[cursorPositionY].substring(0, min(cursorPositionX, lines[cursorPositionY].length))

				cursorPositionX = 0
				cursorPositionY++

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.LEFT)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				if (cursorPositionX > 0)
					cursorPositionX--

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.RIGHT)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				if (cursorPositionX < lines[cursorPositionY].length)
					cursorPositionX++

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.UP)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				if (cursorPositionY > 0) {
					cursorPositionX = calculateNewCursorPosition(draw, lines[cursorPositionY].substring(0, cursorPositionX), lines[cursorPositionY - 1])
					cursorPositionY--
				}

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.DOWN)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				if (cursorPositionY + 1 < lines.size) {
					cursorPositionX = calculateNewCursorPosition(draw, lines[cursorPositionY].substring(0, cursorPositionX), lines[cursorPositionY + 1])
					cursorPositionY++
				}

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.HOME)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				cursorPositionX = 0

			} else if (keyStroke.hasKey(KeyboardEvent.Keys.END)) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.SHIFT))
					enableMarking()
				else
					marking = false

				cursorPositionX = lines[cursorPositionY].length

			} else if (keyStroke.character != null) { // User types an actual letter to push into the text field
				if (marking)
					removeText(getSelectionRange())

				val text = lines[cursorPositionY]
				lines[cursorPositionY] = text.substring(0, cursorPositionX) + keyStroke.character + text.substring(cursorPositionX, text.length)
				cursorPositionX++
			}
		}

		keyStrokeQueue.clear()
	}

	private fun calculateNewCursorPosition(draw: Draw, from: String, to: String): Int {
		val fromWidth = draw.getTextWidth(from)
		val toWidth = draw.getTextWidth(to)

		if (fromWidth >= toWidth)
			return to.length

		// We try in a silly way to get the matching with in the to-string.
		var current = to.length
		while (current > 0 && draw.getTextWidth(to.substring(0, current)) >= fromWidth)
			current--

		return current
	}

	private fun enableMarking() {
		if (!marking) {
			marking = true
			markerBeginX = cursorPositionX
			markerBeginY = cursorPositionY
		}
	}

	fun getSelectionRange(): SelectionRange {
		if (!marking)
			return SelectionRange(0, 0, 0, 0)

		val startX = when {
			cursorPositionY > markerBeginY -> markerBeginX
			cursorPositionY < markerBeginY -> cursorPositionX
			else -> min(cursorPositionX, markerBeginX)
		}
		val stopX = when {
			cursorPositionY > markerBeginY -> cursorPositionX
			cursorPositionY < markerBeginY -> markerBeginX
			else -> max(cursorPositionX, markerBeginX)
		}
		val startY = min(cursorPositionY, markerBeginY)
		val stopY = max(cursorPositionY, markerBeginY)

		return SelectionRange(startX, startY, stopX, stopY)
	}

	fun removeText(selectionRange: SelectionRange) {
		if (selectionRange.startY >= lines.size || selectionRange.stopY >= lines.size)
			throw RuntimeException("Invalid selection")

		if (selectionRange.startX > lines[selectionRange.startY].length)
			throw RuntimeException("Invalid selection")

		if (selectionRange.stopX > lines[selectionRange.stopY].length)
			throw RuntimeException("Invalid selection")

		// Trim the first and last line (they could be the same line for 1 line selection)
		val startText = lines[selectionRange.startY].substring(0, selectionRange.startX)
		val stopText = lines[selectionRange.stopY].substring(selectionRange.stopX)

		// Delete all lines in selection
		for (i in selectionRange.startY..selectionRange.stopY)
			lines.removeAt(selectionRange.startY)

		// Add back a final line with the merged texts
		lines.add(selectionRange.startY, startText + stopText)

		marking = false

		cursorPositionX = selectionRange.startX
		cursorPositionY = selectionRange.startY
	}

	override fun onEvent(event: UIEvent) {
		mouseHandler.handle(event)
	}
}