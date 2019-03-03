package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.PopupLabel
import net.merayen.elastic.ui.objects.components.dragdrop.TargetItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem
import net.merayen.elastic.ui.objects.top.views.filebrowserview.FileListItemDragable
import net.merayen.elastic.util.NodeUtil

class NodeViewDropTarget(private val nodeview: NodeView) : TargetItem(nodeview.container) {
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

			val p = nodeview.container.getRelativeFromAbsolute(mouseEvent.x.toFloat(), mouseEvent.y.toFloat())

			val nodeId = NodeUtil.createID()
			nodeview.sendMessage(CreateNodeMessage(nodeId, "sample", 1, nodeview.currentNodeId))
			nodeview.sendMessage(NodeParameterMessage(nodeId, "ui.java.translation.x", p.x))
			nodeview.sendMessage(NodeParameterMessage(nodeId, "ui.java.translation.y", p.y))
			println(p)
			//(nodeview.search.top as Top).mouseCursorManager.retrieveCarryItem(mouseEvent.id)
		}
	}
}