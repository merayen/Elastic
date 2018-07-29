package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.objects.components.PopupLabel
import net.merayen.elastic.ui.objects.components.dragdrop.TargetItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.objects.top.views.filebrowserview.FileListItemDragable

class NodeViewDropTarget(nodeview: NodeView) : TargetItem(nodeview.container) {
	var interested = false
	var hover = false
	private val popupLabel = PopupLabel("Yoyoyo!")

	override fun onInterest(item: MouseCarryItem) {
		if(item is FileListItemDragable)
			interested = true
	}

	override fun onBlurInterest() {
		interested = false
	}

	override fun onHover(item: MouseCarryItem) {
		if(item is FileListItemDragable)
			hover = true
	}

	override fun onBlur() {
		hover = false
	}

	override fun onDrop(item: MouseCarryItem) {
		if(item is FileListItemDragable) {
			hover = false
		}
	}
}