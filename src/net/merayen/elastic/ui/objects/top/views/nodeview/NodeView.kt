package net.merayen.elastic.ui.objects.top.views.nodeview

import java.util.ArrayList

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.controller.NodeViewController
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.MouseWheelEvent
import net.merayen.elastic.ui.objects.UINet
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.ui.util.Movable
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.TaskExecutor

// TODO accept NetList as input and rebuild ourselves automatically from that
// TODO allow forwarding of node messages from and to the backend.
// TODO make (dis)connecting work again, by sending a message when user tries

/**
 * Main view. Shows all the nodes and their connections.
 */
class NodeView : View() {
	/**
	 * Retrieve the ID of the node this view displays.
	 * @return
	 */
	var currentNodeId: String? = null
		private set

	val container = NodeViewContainer(this)
	val uiNet: UINet
	private val movable: Movable
	private val nodes = ArrayList<UINode>()
	private val nodeViewBar = NodeViewBar()
	var nodeViewController: NodeViewController? = null // Automatically set by NodeViewController

	private var contextMenu: NodeViewContextMenu? = null

	private var dragAndDropTarget = NodeViewDropTarget(this)

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
				container.zoomTranslateXTarget = container.translation.x
				container.zoomTranslateYTarget = container.translation.y
				container.zoomScaleXTarget = container.translation.scaleX
				container.zoomScaleYTarget = container.translation.scaleY
			}
		})
	}

	override fun onInit() {
		super.onInit()

		// Sends a message that will be picked up by NodeViewController, which again will register us
		sendMessage(NodeViewController.Hello())
	}

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

	/**
	 * Add a node.
	 * Node must already be existing in the backend.
	 */
	fun addNode(node_id: String, name: String, version: Int?) {
		val path = String.format(UI_CLASS_PATH, name, version, "UI")

		val uinode: UINode
		try {
			uinode = Class.forName(path).getDeclaredConstructor().newInstance() as UINode
		} catch (e: Exception) {
			throw RuntimeException(e)
		}

		uinode.nodeId = node_id

		nodes.add(uinode)
		container.add(uinode)
	}

	fun removeNode(node_id: String) {
		val uinode = getNode(node_id)
		nodes.remove(uinode)
		container.remove(uinode!!)
	}

	fun getNodes(): ArrayList<UINode> {
		return ArrayList(nodes)
	}

	fun getNode(id: String): UINode? {
		for (x in nodes)
			if (x.nodeId == id)
				return x

		return null
	}

	fun messageNode(node_id: String, message: Postmaster.Message) {
		val node = getNode(node_id)

		if (node == null) {
			System.out.printf("WARNING: Node with id %s not found in this NodeView. Out of sync?\n", node_id)
			return
		}

		node.executeMessage(message)
	}

	private fun zoom(newScaleX: Float, newScaleY: Float) {
		/*val previousScaleX = container.zoomScaleXTarget
		val previousScaleY = container.zoomScaleYTarget*/
		val previousScaleX = container.translation.scaleX
		val previousScaleY = container.translation.scaleY
		val scaleDiffX = newScaleX - previousScaleX
		val scaleDiffY = newScaleY - previousScaleY
		/*val currentOffsetX = container.zoomTranslateXTarget - getWidth() / 2
		val currentOffsetY = container.zoomTranslateYTarget - getHeight() / 2*/
		val currentOffsetX = container.translation.x - getWidth() / 2
		val currentOffsetY = container.translation.y - getHeight() / 2

		container.zoomScaleXTarget = newScaleX
		container.zoomScaleYTarget = newScaleY
		container.zoomTranslateXTarget = getWidth() / 2 + currentOffsetX + currentOffsetX * (-scaleDiffX / newScaleX)
		container.zoomTranslateYTarget = getHeight() / 2 + currentOffsetY + currentOffsetY * (-scaleDiffY / newScaleY)
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
						Math.max(Math.min(sX, 10f), 0.1f),
						Math.max(Math.min(sY, 10f), 0.1f)
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
		currentNodeId = newNodeId
		sendMessage(NetListRefreshRequestMessage())
	}

	fun reset() {
		//System.out.printf("NodeView reset(): %s\n", this)

		uiNet.reset()

		for (node in nodes)
			container.remove(node)

		// Set up context menu when right-clicking on the background
		if (contextMenu != null)
			container.remove(contextMenu!!)

		if (container.search.children.size != 1) // Only UINet() should be remaining
			throw RuntimeException("Should not happen")

		nodes.clear()

		contextMenu = NodeViewContextMenu(container, currentNodeId)
		container.add(contextMenu!!)
	}

	companion object {
		private const val UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.%s_%d.%s"
	}
}
