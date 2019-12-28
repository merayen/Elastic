package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.midi.MidiMessagesCreator
import net.merayen.elastic.backend.midi.MidiState
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.SelectionRectangle
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint
import net.merayen.elastic.util.UniqueID

class PianoNet(private val octaveCount: Int) : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * Called when a ghost note is moved over the piano net.
		 * This should mark the piano tangent that correspond to the tangent argument.
		 */
		fun onGhostNote(tangent: Short)

		/**
		 * Ghost note disappears. Piano should remove the mark.
		 */
		fun onGhostNoteOff()

		/**
		 * User has added a note to the sheet.
		 */
		fun onAddMidi(midiData: MidiData)

		fun onRemoveMidi(id: String)
	}

	private enum class ToolModes {
		Drag, Select, Create, Line
	}

	/**
	 * Width of the piano net. Do not set. It gets automatically set based on beatCount.
	 */
	override var layoutWidth = 100f

	/**
	 * Do not set the height. It gets set automatically based on octaveWidth.
	 */
	override var layoutHeight = 100f

	var handler: Handler? = null

	private var toolMode = ToolModes.Drag

	private val mouseHandler = MouseHandler(this)
	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	private val contextMenuItemCreateNote = TextContextMenuItem("Create note here")
	private val contextMenuItemSelectionRectangle = TextContextMenuItem("Select tool")
	private val contextMenuItemDrag = TextContextMenuItem("Drag tool")
	private val contextMenuItemLine = TextContextMenuItem("Line tool")

	/**
	 * Vertical size of 1 octave, in height units
	 */
	var octaveWidth = 5f * 7f

	/**
	 * How many width units one beat is
	 */
	var beatWidth = 10f

	/**
	 * Width in beats.
	 */
	var beatCount = 0f

	/**
	 * How notes should snap.
	 * 1 == snap to each beat.
	 * 4f == snap to each bar, if 4 beats per bar
	 */
	var snapQuantization = 1.0

	private val selectionRectangle = SelectionRectangle(this)

	private val BLACK_TANGENTS = arrayOf(false, true, false, true, false, true, false, false, true, false, true, false)

	private val notes = PianoNetNotes(octaveCount, this)


	override fun onInit() {
		selectionRectangle.handler = object : SelectionRectangle.Handler {
			override fun onDrag() {
				println("Yapp!")
			}

			override fun onDrop() {
				println("Yop")
			}
		}
		add(selectionRectangle)

		contextMenu.addMenuItem(contextMenuItemCreateNote)
		contextMenu.addMenuItem(contextMenuItemSelectionRectangle)
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(contextMenuItemDrag)
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(contextMenuItemLine)

		contextMenu.handler = object : ContextMenu.Handler {
			override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
				when (item) {
					contextMenuItemCreateNote -> {
						toolMode = ToolModes.Create
					}
					contextMenuItemSelectionRectangle -> {
						toolMode = ToolModes.Select
					}
					contextMenuItemDrag -> {
						toolMode = ToolModes.Drag
					}
					contextMenuItemLine -> {
						toolMode = ToolModes.Line
					}
				}
			}

			override fun onMouseDown(position: MutablePoint) {
				//selectionRectangle.cancel()
			}
		}

		mouseHandler.setHandler(object : MouseHandler.Handler() {
			private val ghostNote = notes.Note("ghostNote", 0.0, 0.0, 0, 1f)

			override fun onMouseMove(position: MutablePoint) {
				if (toolMode == ToolModes.Create) {
					if (ghostNote.parent == null)
						notes.add(ghostNote)

					ghostNote.alpha = 0.5f
					ghostNote.start = ((position.x / beatWidth) / snapQuantization).toInt() * snapQuantization
					ghostNote.length = 1.0
					ghostNote.tangent = ((octaveCount * octaveWidth - (position.y - octaveWidth / 12f)) / (octaveWidth / 12f)).toShort()

					handler?.onGhostNote(ghostNote.tangent)
				}
			}

			override fun onMouseOut() {
				if (ghostNote.parent != null)
					notes.remove(ghostNote)

				handler?.onGhostNoteOff()
			}

			override fun onMouseClick(position: MutablePoint?) {
				if (ghostNote.parent != null) {
					notes.remove(ghostNote)
					if (toolMode == ToolModes.Create) {
						val midiData = MidiData()
						midiData.add(MidiData.MidiChunk(
								UniqueID.create(),
								ghostNote.start,
								MidiMessagesCreator.keyDown(ghostNote.tangent.toInt(), 1f).toMutableList()
						))
						midiData.add(MidiData.MidiChunk(
								UniqueID.create(),
								ghostNote.start + ghostNote.length,
								MidiMessagesCreator.keyUp(ghostNote.tangent.toInt(), 0f).toMutableList()
						))
						handler?.onAddMidi(midiData)
					}
					handler?.onGhostNoteOff()
				}
			}
		})
		add(notes)
	}

	override fun onUpdate() {
		layoutWidth = beatWidth * beatCount
		notes.layoutWidth = layoutWidth
		notes.layoutHeight = layoutHeight

		notes.octaveWidth = octaveWidth
		notes.beatWidth = beatWidth
	}

	override fun onDraw(draw: Draw) {
		drawLines(draw)
		drawBars(draw)
	}

	private fun drawLines(draw: Draw) {
		var y = 0f

		draw.setStroke(0.5f)

		var pos = 0
		for (i in 0 until octaveCount * 12) {
			val b = BLACK_TANGENTS[pos]

			if (b)
				draw.setColor(0.2f, 0.2f, 0.2f)
			else
				draw.setColor(0.3f, 0.3f, 0.3f)

			draw.fillRect(0f, y, layoutWidth, y + octaveWidth / 24)

			y += octaveWidth / 12
			pos++
			pos %= 12
		}

		layoutHeight = y
	}

	private fun drawBars(draw: Draw) {
		draw.setStroke(1f)
		draw.setColor(0.1f, 0.1f, 0.1f)
		var x = 0f
		for (i in 0 until (layoutWidth / beatWidth).toInt() + 1) { // TODO don't use "0 until layoutWidth", only draw what is visible
			draw.line(x, 0f, x, layoutHeight)
			x += beatWidth
		}
	}

	override fun onEvent(event: UIEvent) {
		if (toolMode == ToolModes.Select)
			selectionRectangle.handle(event)

		mouseHandler.handle(event)

		contextMenu.handle(event)
	}

	fun loadMidi(midiData: MidiData) {
		val midiState = object : MidiState() {
			override fun onKeyUp(tangent: Short) {
				val activeTangent = activeKeys[tangent]

				if (activeTangent?.id == null) {
					println("WARNING: KeyUp on a key not pushed, or missing id")
					return
				}

				val note = notes.Note(activeTangent.id, activeTangent.start, time - activeTangent.start, activeTangent.tangent, activeTangent.velocity)
				notes.add(note)
			}
		}

		for (midiChunk in midiData)
			midiState.handle(midiChunk)
	}
}
