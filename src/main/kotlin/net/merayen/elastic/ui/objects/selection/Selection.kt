package net.merayen.elastic.ui.objects.selection

import net.merayen.elastic.ui.Rect
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.SelectionRectangle
import net.merayen.elastic.ui.util.boolean

/**
 * Draws a selection rectangle that will call onSelect() whenever the rectangle hits an object inside source.
 * @param uiobject Where the items to be selected lays. Rectangle is drawn inside this object
 * @param trigger Triggers the rectangle when holding down the mouse button
 * @param button Which button to trigger on
 */
class Selection(private val uiobject: UIObject, private val trigger: UIObject, button: MouseEvent.Button = MouseEvent.Button.LEFT) {
	interface Handler {
		/**
		 * An UIObject has been selected.
		 */
		fun onSelect(uiobject: UIObject)

		/**
		 * An UIObject has been unselected.
		 */
		fun onUnselect(uiobject: UIObject)

		/**
		 * User has started to drag the SelectionRectangle.
		 */
		fun onStartDragging()

		/**
		 * User is done selecting and has let go of the mouse button.
		 */
		fun onDrop()
	}

	private val selectionRectangle = SelectionRectangle(trigger, button)

	var handler: Handler? = null

	private val selected = ArrayList<UIObject>()

	init {
		selectionRectangle.handler = object : SelectionRectangle.Handler {
			private var dragging = false

			override fun onDrag() {
				if (!dragging) {
					dragging = true
					handler?.onStartDragging()
				}

				check()
			}

			override fun onDrop() {
				dragging = false
				handler?.onDrop()
			}
		}

		uiobject.add(selectionRectangle)
	}

	private fun check() {
		val handler = handler ?: return

		val selection = Rect(selectionRectangle.translation.x, selectionRectangle.translation.y, selectionRectangle.getWidth(), selectionRectangle.getHeight())

		for (obj in uiobject.children) {
			if (obj === selectionRectangle)
				continue

			val pos = selectionRectangle.getRelativePosition(obj) ?: continue

			// TODO take care of scaling of obj

			val collision = boolean(
				selection,
				Rect(pos.x, pos.y, obj.getWidth(), obj.getHeight())
			)

			if (collision.width > 0 && collision.height > 0) {
				if (obj !in selected) {
					selected.add(obj)
					handler.onSelect(obj)
				}
			} else if (obj in selected) {
				selected.remove(obj)
				handler.onUnselect(obj)
			}
		}

		selected.removeIf { it.parent !== uiobject }
	}

	fun handle(event: UIEvent) = selectionRectangle.handle(event)
}