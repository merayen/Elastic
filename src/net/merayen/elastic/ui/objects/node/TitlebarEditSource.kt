package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.dragdrop.PopupLabel
import net.merayen.elastic.ui.objects.components.dragdrop.SourceItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

class TitlebarEditSource(source: UIObject, val node: UINode) : SourceItem(source) {

	override fun onGrab(): MouseCarryItem {
		return EditNodeMouseCarryItem(node)
	}

	override fun onDrop() {}
}