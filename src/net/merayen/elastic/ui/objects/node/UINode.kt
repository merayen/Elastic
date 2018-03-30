package net.merayen.elastic.ui.objects.node

import java.util.ArrayList

import net.merayen.elastic.ui.Draw
import org.json.simple.JSONObject

import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.system.intercom.RemoveNodeMessage
import net.merayen.elastic.system.intercom.RemoveNodePortMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView

abstract class UINode : UIObject() {
	var node_id: String? = null // Same ID as in the backend-system, netlist etc
	var layoutWidth = 500f
	var layoutHeight = 500f

	protected var titlebar: Titlebar
		private set

	private val nodePorts = ArrayList<UIPort>()

	protected val ports
		get() = ArrayList<UIPort>(nodePorts)

	private var inited: Boolean = false

	// Dumping and restoring of simple things like position and scaling. All other dumping should be done by GlueNode (and maybe netnode too)
	protected open fun onDump(state: JSONObject) {}

	protected open fun onRestore(state: JSONObject) {}

	protected abstract fun onCreatePort(port: UIPort)  // Node can customize the created UIPort in this function
	protected abstract fun onRemovePort(port: UIPort)  // Node can clean up any resources belonging to the UIPort
	protected abstract fun onMessage(message: NodeParameterMessage)
	protected abstract fun onData(message: NodeDataMessage)
	protected abstract fun onParameter(key: String, value: Any)

	val UINet
		get() = search.parentByType(NodeView::class.java)?.uiNet

	init {
		titlebar = Titlebar()
		add(titlebar)
	}

	override fun onInit() {
		inited = true
	}

	override fun onDraw(draw: Draw) {
		if (!inited)
			throw RuntimeException("Forgotten super.onInit() ?")

		draw.setColor(80, 80, 80)
		draw.fillRect(-1f, -1f, layoutWidth + 2f, layoutHeight + 2f)

		draw.setColor(100, 100, 100)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(180, 180, 180)
		draw.fillRect(1f, 1f, layoutWidth - 2f, layoutHeight - 2f)

		draw.setColor(100, 100, 100)
		draw.fillRect(2f, 2f, layoutWidth - 4f, layoutHeight - 4f)

		titlebar.layoutWidth = layoutWidth

		super.onDraw(draw)
	}

	fun getPort(name: String): UIPort {
		for (x in nodePorts)
			if (x.name == name)
				return x

		throw RuntimeException("Port doesn't exist");
	}

	fun hasPort(name: String) = nodePorts.stream().anyMatch {it.name == name}

	fun sendParameter(key: String, value: Any) {
		sendMessage(NodeParameterMessage(node_id, key, value))
	}

	fun sendData(value: Map<String, Any>) {
		sendMessage(NodeDataMessage(node_id, value))
	}

	fun executeMessage(message: Postmaster.Message) {
		if (message is NodeParameterMessage) {

			if (message.key == "ui.java.translation.x" && !titlebar.isDragging)
				translation.x = (message.value as Number).toFloat()
			else if (message.key == "ui.java.translation.y" && !titlebar.isDragging)
				translation.y = (message.value as Number).toFloat()

			onMessage(message)
			onParameter(message.key, message.value)

		} else if (message is NodeDataMessage) {
			onData(message)

		} else if (message is CreateNodePortMessage) {
			val port = UIPort(message.port, message.output, this)
			nodePorts.add(port)
			add(port)
			onCreatePort(port)

		} else if (message is RemoveNodePortMessage) {
			val port = getPort(message.port)
			remove(port!!)
			nodePorts.remove(port)
			onRemovePort(port)
		}
	}

	/**
	 * Request to delete ourself. Only a message will be sent to backend, which will delete us.
	 */
	fun delete() {
		sendMessage(RemoveNodeMessage(node_id))
	}
}
