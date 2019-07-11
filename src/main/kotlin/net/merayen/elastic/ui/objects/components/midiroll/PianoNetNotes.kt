package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class PianoNetNotes(private val octaveCount: Int) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onSelect(id: String)
		fun onChange(id: String)
	}

	var handler: Handler? = null

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	/**
	 * Width of a beat. Used to place the notes correctly in the horizontal axis.
	 * Must be updated from PianoNet.
	 */
	var beatWidth = 0f

	/**
	 * Width of an octave.
	 * Must be updated from PianoNet.
	 */
	var octaveWidth = 0f

	/**
	 * If dirty, rearranges all the notes.
	 */
	private var dirty = true

	/**
	 * A note, represented as a simple colored bar in the UI.
	 * @param id The id of the note
	 * @param start Where the note start in beats
	 * @param length Length of the note in beats
	 * @param tangent Which tangent on the piano
	 * @param weight How much weight
	 */
	inner class Note(val id: String, var start: Float, var length: Float, var tangent: Int, var weight: Float) : UIObject(), FlexibleDimension {
		override var layoutWidth = 0f
		override var layoutHeight = 0f

		var selected = false
		var alpha = 1f

		private val mouseHandler = MouseHandler(this)

		private val contextMenu = ContextMenu(this, 4, MouseEvent.Button.RIGHT)

		override fun onInit() {
			mouseHandler.setHandler(object : MouseHandler.Handler() {
				override fun onMouseClick(position: Point?) {
					handler?.onSelect(id)
				}
			})

			contextMenu.addMenuItem(EmptyContextMenuItem())
			contextMenu.addMenuItem(EmptyContextMenuItem())
			contextMenu.addMenuItem(TextContextMenuItem("Remove"))
		}

		override fun onDraw(draw: Draw) {
			if (alpha < 1f)
				draw.disableOutline()

			draw.setColor(weight, if (selected) 0.7f else 0.5f, if (selected) 0.7f else 0.5f, alpha)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

			draw.setColor(weight, 0.7f, 0.7f, alpha)
			draw.setStroke(1f)
			draw.rect(0f, 0f, layoutWidth, layoutHeight)
		}

		override fun onEvent(event: UIEvent) {
			mouseHandler.handle(event)
		}
	}

	override fun add(uiobject: UIObject) {
		if (uiobject.parent != null)
			throw RuntimeException("Note should not have a parent")

		if (uiobject !is Note)
			throw RuntimeException("Only Note()-instances can be added to PianoNetNotes")

		super.add(uiobject)
	}

	override fun onUpdate() {
		rearrangeNotes() // TODO Only if any changes has happened
	}

	fun rearrangeNotes() {
		for (obj in children) {
			if (obj is Note) {
				obj.translation.x = obj.start * beatWidth
				obj.translation.y = (octaveCount * 12 - obj.tangent) * (octaveWidth / 12f)

				obj.layoutWidth = obj.length * beatWidth
				obj.layoutHeight = octaveWidth / 12f
			}
		}
	}
}