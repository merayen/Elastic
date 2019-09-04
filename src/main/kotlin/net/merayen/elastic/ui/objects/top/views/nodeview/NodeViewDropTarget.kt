package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.backend.nodes.createNewNodeProperties
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
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
		if (item is FileListItemDragable)
			interested = true
	}

	override fun onBlurInterest() {
		interested = false
	}

	override fun onHover(item: MouseCarryItem) {
		if (item is FileListItemDragable)
			hover = true
	}

	override fun onBlur() {
		hover = false
	}

	override fun onDrop(item: MouseCarryItem) {
		if (item is FileListItemDragable) {
			hover = false

			val p = nodeview.container.getRelativeFromAbsolute(mouseEvent.x.toFloat(), mouseEvent.y.toFloat())

			val name = "sample"
			val version = 1

			val data = createNewNodeProperties(name, version)
			data.uiTranslation = BaseNodeProperties.UITranslation(x = p.x, y = p.y)

			val nodeId = NodeUtil.createID()
			nodeview.sendMessage(CreateNodeMessage(nodeId, name, version, nodeview.currentNodeId))
			nodeview.sendMessage(NodePropertyMessage(nodeId, data))
			// TODO implement actual sending of the filename to the node, for import
			//(nodeview.search.top as Top).mouseCursorManager.retrieveCarryItem(mouseEvent.id)
		}
	}
}