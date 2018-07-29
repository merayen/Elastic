package net.merayen.elastic.ui.objects.top.views.filebrowserview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.dragdrop.SourceItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

class FileListItemDragable : UIObject() {

	val sourceItem = object : SourceItem(this) {
		override fun onGrab(): MouseCarryItem {
			return object : MouseCarryItem() {
				override fun onDraw(draw: Draw) {
					draw.setColor(255, 0, 255)
					draw.fillRect(0f, 0f, 5f, 5f)
				}
			}
		}

		override fun onDrop() {

		}

	}

	override fun onInit() {
		add(sourceItem.source)
	}
}