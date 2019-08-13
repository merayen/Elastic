package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.backend.nodes.createNewNodeData
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.util.NodeUtil
import java.util.*

abstract class UINode : UIObject(), FlexibleDimension {
	lateinit var nodeId: String // Same ID as in the backend-system, netlist etc
	override var layoutWidth = 100f
	override var layoutHeight = 80f

	protected val titlebar = Titlebar()

	private val nodePorts = ArrayList<UIPort>()

	protected val ports
		get() = ArrayList<UIPort>(nodePorts)

	private var inited: Boolean = false

	protected abstract fun onCreatePort(port: UIPort)  // Node can customize the created UIPort in this function
	protected abstract fun onRemovePort(port: UIPort)  // Node can clean up any resources belonging to the UIPort
	protected abstract fun onMessage(message: BaseNodeData) // TODO remove. Use onParameter instead!
	protected abstract fun onData(message: NodeDataMessage)
	protected abstract fun onParameter(instance: BaseNodeData)

	val UINet
		get() = search.parentByType(NodeView::class.java)?.uiNet

	override fun onInit() {
		titlebar.handler = object : Titlebar.Handler {
			override fun onMove() {
				sendUiData()
			}
		}
		add(titlebar)

		inited = true
	}

	override fun onDraw(draw: Draw) {
		if (!inited)
			throw RuntimeException("Forgotten super.onInit() ?")

		draw.setColor(80, 80, 80)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		titlebar.layoutWidth = layoutWidth

		super.onDraw(draw)
	}

	fun getPort(name: String): UIPort? {
		for (x in nodePorts)
			if (x.name == name)
				return x

		return null
	}

	fun sendParameter(instance: BaseNodeData) {
		sendMessage(NodeParameterMessage(nodeId, instance))
	}

	fun sendData(value: NodeDataMessage) {
		sendMessage(value)
	}

	fun executeMessage(message: Any) {
		if (message is NodeParameterMessage) {
			val properties = message.instance
			if (properties is BaseNodeData) {
				if (properties.uiTranslation != null)

					if (!titlebar.isDragging) {
						val newTranslation = message.instance.uiTranslation
						val x = newTranslation?.x
						val y = newTranslation?.y

						if (x != null)
							translation.x = x

						if (y != null)
							translation.y = y
					}
			}

			onMessage(message.instance)
			onParameter(message.instance)

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
		} else if (message is NodeStatusMessage)
			TODO()
	}

	private fun sendUiData() {
		val data = newNodeData()
		data.uiTranslation = BaseNodeData.UITranslation(translation.x, translation.y)
		sendParameter(data)
	}

	protected fun newNodeData(): BaseNodeData {
		val path = this::class.qualifiedName!!.split(".")
		val name = NodeUtil.getNodeName(path[path.size - 2])
		val version = NodeUtil.getNodeVersion(path[path.size - 2])
		return createNewNodeData(name, version)
	}
}
