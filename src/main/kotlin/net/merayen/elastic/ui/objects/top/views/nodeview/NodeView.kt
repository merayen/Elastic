package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.NodeViewController
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.MouseWheelEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UINet
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.Revision
import java.util.*
import kotlin.math.max
import kotlin.math.min


/**
 * Main view. Shows all the nodes and their connections.
 */
class NodeView : View(), Revision {
	/**
	 * Retrieve the ID of the node this view displays.
	 * @return
	 */
	var currentNodeId: String? = null
		private set

	val container = NodeViewContainer(this)
	val uiNet: UINet
	private val movable: Movable
	val nodes = HashMap<String, UINode>()
	private val nodeViewBar = NodeViewBar(this)
	var nodeViewController: NodeViewController? = null // Automatically set by NodeViewController

	private var contextMenu: NodeViewContextMenu? = null

	private var dragAndDropTarget = NodeViewDropTarget(this)

	private var loaded = false

	override var revision = 0
		private set

	init {
		add(container)
		add(nodeViewBar)

		uiNet = UINet()
		container.add(uiNet, 0) // Add the net first (also, drawn behind everything), as addNode() might have already been called

		// Make it possible to move NodeViewContainer by dragging the background
		movable = Movable(container, container, MouseEvent.Button.LEFT)
		movable.setHandler(object : Movable.IMoveable {
			override fun onGrab() {}
			override fun onDrop() {}
			override fun onMove() {
				container.translateXTarget = container.translation.x
				container.translateYTarget = container.translation.y
				container.zoomScaleXTarget = container.translation.scaleX
				container.zoomScaleYTarget = container.translation.scaleY
			}
		})
	}

	private val easyMotion = NodeViewEasyMotion(this)
	override val easyMotionBranch = easyMotion.createEasyMotionBranch()

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		when {
			dragAndDropTarget.hover -> draw.setColor(100, 100, 200)
			dragAndDropTarget.interested -> draw.setColor(50, 50, 100)
			else -> draw.setColor(20, 20, 50)
		}

		draw.fillRect(2f, 2f, getWidth() - 4, getHeight() - 4)

		nodeViewBar.layoutWidth = getWidth()
	}

	override fun onUpdate() {
		super.onUpdate()

		// If we do not have any node id to display, we try to fetch top most one
		if (currentNodeId == null) {
			val topNodeId = nodeViewController?.topNodeId
			if (topNodeId != null)
				swapView(topNodeId)
		}

		if (!loaded && currentNodeId != null) {
			loaded = true
			val nodeViewController = nodeViewController
			if (nodeViewController != null)
				for (message in nodeViewController.getNetListRefreshMessages())
					handleMessage(message)

			attachContextMenu()
		}
	}

	fun handleMessage(message: ElasticMessage) {
		if (!loaded)
			return // Any message we receive before being loaded are not valid

		val netList = nodeViewController!!.netList

		val netListUtil = NetListUtil(netList)

		// Nodes
		when (message) {
			is CreateNodeMessage -> {
				if (message.parent == currentNodeId)
					addNode(message.node_id, message.name, message.version)
			}
			is RemoveNodeMessage -> {
				removeNode(message.node_id)
			}
			is NodeMessage -> nodes[message.nodeId]?.executeMessage(message)
		}

		// UINet
		when (message) {
			is NodeConnectMessage -> {
				val nodeAParent = netListUtil.getParent(netList.getNode(message.node_a))
				val nodeBParent = netListUtil.getParent(netList.getNode(message.node_b))

				if (nodeAParent != nodeBParent)
					throw RuntimeException("Should not happen")

				if (nodeAParent.id == currentNodeId)
					uiNet.handleMessage(message) // Forward message regarding the net, from backend to the UINet, to all NodeViews
			}
			is NodeDisconnectMessage -> {
				val nodeAParent = netListUtil.getParent(netList.getNode(message.node_a))
				val nodeBParent = netListUtil.getParent(netList.getNode(message.node_b))

				if (nodeAParent != nodeBParent)
					throw RuntimeException("Should not happen")

				if (nodeAParent.id == currentNodeId)
					uiNet.handleMessage(message) // Forward message regarding the net, from backend to the UINet, to all NodeViews
			}
			is RemoveNodeMessage -> {
				if (currentNodeId == message.nodeId)
					disable()
				else
					uiNet.handleMessage(message) // Send to every uinet anyway
			}
			is RemoveNodePortMessage -> {
				val nodeParent = netListUtil.getParent(netList.getNode(message.nodeId))

				if (nodeParent.id == currentNodeId)
					uiNet.handleMessage(message)
			}
		}
	}

	/**
	 * Add a node.
	 * Node must already be existing in the backend.
	 */
	private fun addNode(node_id: String, name: String, version: Int?) {
		val path = String.format(UI_CLASS_PATH, name, version, "UI")

		val uinode: UINode
		try {
			uinode = Class.forName(path).getDeclaredConstructor().newInstance() as UINode
		} catch (e: Exception) {
			throw RuntimeException(e)
		}

		uinode.nodeId = node_id

		// Unmark all nodes. Not implemented selecting anyway
		for (node in nodes.values)
			node.selected = false

		nodes[node_id] = uinode
		container.add(uinode)
		revision++
	}

	private fun removeNode(node_id: String) {
		val removedNode = nodes.remove(node_id) ?: return
		container.remove(removedNode)
		revision++
	}

	private fun zoom(newScaleX: Float, newScaleY: Float) {
		val previousScaleX = container.translation.scaleX
		val previousScaleY = container.translation.scaleY
		val scaleDiffX = newScaleX - previousScaleX
		val scaleDiffY = newScaleY - previousScaleY
		val currentOffsetX = container.translation.x - getWidth() / 2
		val currentOffsetY = container.translation.y - getHeight() / 2

		container.zoomScaleXTarget = newScaleX
		container.zoomScaleYTarget = newScaleY
		container.translateXTarget = getWidth() / 2 + currentOffsetX + currentOffsetX * (-scaleDiffX / newScaleX)
		container.translateYTarget = getHeight() / 2 + currentOffsetY + currentOffsetY * (-scaleDiffY / newScaleY)
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)

		movable.handle(event)
		dragAndDropTarget.handle(event)

		if (event is MouseWheelEvent) {

			if (isFocused) { // Decides if we are the receiver of the event
				var sX = container.zoomScaleXTarget
				var sY = container.zoomScaleYTarget

				if (event.offsetY < 0) {
					sX /= 1.1f
					sY /= 1.1f
				} else if (event.offsetY > 0) {
					sX *= 1.1f
					sY *= 1.1f
				}

				zoom(
					max(min(sX, 10f), 0.1f),
					max(min(sY, 10f), 0.1f)
				)
			}
		}
	}

	override fun cloneView(): View {
		val nv = NodeView()

		val currentNodeId = currentNodeId
		if (currentNodeId != null)
			nv.swapView(currentNodeId)

		nv.container.translation.x = container.translation.x
		nv.container.translation.y = container.translation.y
		nv.container.translation.scaleX = container.translation.scaleX
		nv.container.translation.scaleY = container.translation.scaleY
		return nv
	}

	fun swapView(newNodeId: String) {
		// TODO Calculate how we should zoom? In or out based on position to current node?
		currentNodeId = newNodeId

		// TODO zoom and translate to cover all the nodes
		container.translateXTarget = 0f
		container.translateYTarget = 0f
		container.zoomScaleXTarget = 1f
		container.zoomScaleYTarget = 1f

		container.translation.x = -getWidth() / 2
		container.translation.y = -getHeight() / 2
		container.translation.scaleX = .1f
		container.translation.scaleY = .1f

		clear()

		// Load our self again
		loaded = false
	}

	fun clear() {
		uiNet.clear()
		for (node in ArrayList(nodes.keys))
			removeNode(node)
	}

	private fun disable() {
		remove(container)
	}

	private fun attachContextMenu() {
		val contextMenu = contextMenu
		if (contextMenu != null)
			container.remove(contextMenu)

		val newContextMenu = NodeViewContextMenu(container, currentNodeId)
		newContextMenu.handler = object : NodeViewContextMenu.Handler {
			override fun onSolveNodes() {
				NodeViewSolver(nodes.values).solve()
			}
		}
		container.add(newContextMenu)

		this.contextMenu = newContextMenu
	}

	companion object {
		private const val UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.%s_%d.%s"
	}

	/**
	 * Focus something.
	 * An UINode perhaps.
	 */
	fun focus(uiObject: UIObject) {
		val pos = container.getRelativePosition(uiObject) ?: return
		val pos2 = container.getRelativePosition(uiObject, uiObject.getWidth(), uiObject.getHeight()) ?: return

		container.translateXTarget = -pos.x / container.zoomScaleXTarget + layoutWidth / 2 - (pos2.x - pos.x) / 2
		container.translateYTarget = -pos.y / container.zoomScaleYTarget + layoutHeight / 2 - (pos2.y - pos.y) / 2
	}
}
