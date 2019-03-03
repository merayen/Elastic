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
class NodeView constructor(node_id: String? = null) : View() {
	/**
	 * Retrieve the ID of the node this view displays.
	 * @return
	 */
	var currentNodeId: String? = null
		internal set // Node that we show the children of
	private var newNodeId: String? = null
	val container = NodeViewContainer(this)
	val uiNet: UINet
	private val movable: Movable
	private val nodes = ArrayList<UINode>()
	private val nodeViewBar = NodeViewBar()
	var nodeViewController: NodeViewController? = null // Automatically set by NodeViewController

	private var context_menu: NodeViewContextMenu? = null

	private var dragAndDropTarget = NodeViewDropTarget(this)

	init {
		this.newNodeId = node_id
		add(container)
		add(nodeViewBar)

		uiNet = UINet()
		container.add(uiNet, 0) // Add the net first (also, drawn behind everything), as addNode() might have already been called

		// Make it possible to move NodeViewContainer by dragging the background
		movable = Movable(container, container, MouseEvent.Button.LEFT)
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

		nodeViewBar.width = getWidth()
	}

	override fun onUpdate() {
		super.onUpdate()

		val nodeViewController = nodeViewController
		if (nodeViewController != null) {
			if (newNodeId == null)
				newNodeId = nodeViewController.topNodeID

			if (newNodeId != currentNodeId)
				innerSwapView(newNodeId)
		}
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
		val previousScaleX = container.translation.scale_x
		val previousScaleY = container.translation.scale_y
		val scaleDiffX = newScaleX - previousScaleX
		val scaleDiffY = newScaleY - previousScaleY
		val currentOffsetX = container.translation.x - getWidth() / 2
		val currentOffsetY = container.translation.y - getHeight() / 2

		container.translation.scale_x = newScaleX
		container.translation.scale_y = newScaleY
		container.translation.x = getWidth() / 2 + currentOffsetX + currentOffsetX * (-scaleDiffX / newScaleX)
		container.translation.y = getHeight() / 2 + currentOffsetY + currentOffsetY * (-scaleDiffY / newScaleY)
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)

		movable.handle(event)
		dragAndDropTarget.handle(event)

		if (event is MouseWheelEvent) {

			if (isFocused) { // Decides if we are the receiver of the event
				var s_x = container.translation.scale_x
				var s_y = container.translation.scale_y

				if (event.offsetY < 0) {
					s_x /= 1.1f
					s_y /= 1.1f
				} else if (event.offsetY > 0) {
					s_x *= 1.1f
					s_y *= 1.1f
				}

				zoom(
						Math.max(Math.min(s_x, 10f), 0.1f),
						Math.max(Math.min(s_y, 10f), 0.1f)
				)
			}
		}
	}

	override fun cloneView(): View {
		val nv = NodeView(currentNodeId)
		nv.container.translation.x = container.translation.x
		nv.container.translation.y = container.translation.y
		nv.container.translation.scale_x = container.translation.scale_x
		nv.container.translation.scale_y = container.translation.scale_y
		return nv
	}

	private fun innerSwapView(newNodeId: String?) {
		this.currentNodeId = newNodeId

		// Ask for sending a new NetList. We queue it up in the ViewportContainer domain, as several NodeViews might have been created simultaneously, we then only send 1 message
		addTask(TaskExecutor.Task(javaClass, 10) { sendMessage(NetListRefreshRequestMessage()) })
	}

	fun swapView(newNodeId: String) {
		this.newNodeId = newNodeId
	}

	fun reset() {
		System.out.printf("NodeView reset(): %s\n", this)

		uiNet.reset()

		for (node in nodes)
			container.remove(node)

		// Set up context menu when right-clicking on the background
		if (context_menu != null)
			container.remove(context_menu!!)

		if (container.search.children.size != 1)
		// Only UINet() should be remaining
			throw RuntimeException("Should not happen")

		nodes.clear()

		context_menu = NodeViewContextMenu(container, currentNodeId)
		container.add(context_menu!!)
	}

	companion object {
		private val UI_CLASS_PATH = "net.merayen.elastic.uinodes.list.%s_%d.%s"
	}
}
