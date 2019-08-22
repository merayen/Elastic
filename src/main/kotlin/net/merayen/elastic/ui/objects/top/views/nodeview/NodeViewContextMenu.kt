package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.backend.nodes.createNewNodeData
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.views.nodeview.addnode.AddNodePopup
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.util.NodeUtil
import net.merayen.elastic.util.Point

class NodeViewContextMenu(background: UIObject, private val node_id: String?) : UIObject() {
	interface Handler {
		fun onSolveNodes()
	}

	var handler: Handler? = null

	private val menu: ContextMenu

	private val addNodeItem = TextContextMenuItem("Add node")
	private val autoArrangeItem = TextContextMenuItem("Auto-arrange")

	init {
		if (node_id == null)
			throw RuntimeException("nodeId can not be null, we must be based on being inside a node")
		val self = this
		menu = ContextMenu(background, 8, MouseEvent.Button.RIGHT)

		menu.handler = object : ContextMenu.Handler {
			override fun onMouseDown(position: Point) {}

			override fun onSelect(item: ContextMenuItem?, position: Point) { // TODO move stuff below out to a separate class
				if (item === addNodeItem) {
					AddNodePopup(self) { info -> createNode(info, position) }
				} else if (item === autoArrangeItem) {
					handler?.onSolveNodes()
				}
			}
		}

		menu.addMenuItem(addNodeItem)
		menu.addMenuItem(autoArrangeItem)
	}

	private fun createNode(info: BaseInfo, position: Point) {
		val path = info.javaClass.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		val name = path[path.size - 2]

		val node_id = NodeUtil.createID()

		val nodeName = NodeUtil.getNodeName(name)
		val nodeVersion = NodeUtil.getNodeVersion(name)

		sendMessage(CreateNodeMessage(node_id, nodeName, nodeVersion, this.node_id)) // TODO group shall not be null, but

		val baseData = createNewNodeData(nodeName, nodeVersion)
		baseData.uiTranslation = BaseNodeData.UITranslation(position.x, position.y)
		sendMessage(NodeParameterMessage(node_id, baseData))
	}

	override fun onEvent(event: UIEvent) {
		menu.handle(event)
	}
}
