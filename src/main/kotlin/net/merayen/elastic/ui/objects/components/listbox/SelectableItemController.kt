package net.merayen.elastic.ui.objects.components.listbox

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class SelectableItemController(private val container: UIObject) {
	inner class Item : UIObject() {
		var selected = false

		private val mouseHandler = MouseHandler(this)

		override fun onInit() {
			mouseHandler.setHandler(object : MouseHandler.Handler() {
				override fun onMouseClick(position: MutablePoint?) {
					this@SelectableItemController.select(this@Item)
				}
			})
		}

		override fun onDraw(draw: Draw) {
			if (selected)
				draw.setColor(1f, 0f, 1f)
			else
				draw.setColor(0.2f, 0f, 0.2f)

			draw.fillRect(1f, 0f, getWidth(), getHeight())
		}

		override fun onEvent(event: UIEvent) {
			mouseHandler.handle(event)
		}
	}

	fun create(uiobject: UIObject): UIObject {
		val item = Item()
		item.add(uiobject)
		return item
	}

	fun select(item: Item) {

	}
}