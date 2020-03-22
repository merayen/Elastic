package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.backend.nodes.createNewNodeProperties
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.util.MutablePoint
import net.merayen.elastic.util.NodeUtil
import net.merayen.elastic.util.Pacer
import java.util.*

abstract class UINode : UIObject(), FlexibleDimension {
	lateinit var nodeId: String // Same ID as in the backend-system, netlist etc
	override var layoutWidth = 100f
	override var layoutHeight = 80f

	protected val titlebar = TitleBar()

	private val nodePorts = ArrayList<UIPort>()

	val ports
		get() = ArrayList<UIPort>(nodePorts)

	private var inited: Boolean = false

	var selected = false

	val targetLocation = MutablePoint()
	private val lastDraw = Pacer()

	protected abstract fun onCreatePort(port: UIPort)  // Node can customize the created UIPort in this function
	protected abstract fun onRemovePort(port: UIPort)  // Node can clean up any resources belonging to the UIPort
	protected abstract fun onProperties(properties: BaseNodeProperties)
	protected abstract fun onData(message: NodeDataMessage)

	val UINet
		get() = search.parentByType(NodeView::class.java)?.uiNet

	override fun onInit() {
		titlebar.handler = object : TitleBar.Handler {
			override fun onMove() {
				targetLocation.x = translation.x
				targetLocation.y = translation.y
				sendUiData()
			}
		}
		add(titlebar)

		inited = true
	}

	override fun onDraw(draw: Draw) {
		if (!inited)
			throw RuntimeException("Forgotten super.onInit() ?")

		lastDraw.update()

		val diff = lastDraw.getDiff(10f)

		translation.x += (targetLocation.x - translation.x) * diff
		translation.y += (targetLocation.y - translation.y) * diff

		if (selected)
			draw.setColor(150, 150, 150)
		else
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

	fun getPorts() = nodePorts.toList()

	fun sendProperties(instance: BaseNodeProperties) {
		sendMessage(NodePropertyMessage(nodeId, instance))
	}

	fun sendData(value: NodeDataMessage) {
		sendMessage(value)
	}

	fun executeMessage(message: ElasticMessage) {
		if (message is NodePropertyMessage) {
			val properties = message.instance
			if (properties is BaseNodeProperties) {
				if (properties.uiTranslation != null)

					if (!titlebar.isDragging) {
						val newTranslation = message.instance.uiTranslation
						val x = newTranslation?.x
						val y = newTranslation?.y

						if (x != null)
							targetLocation.x = x

						if (y != null)
							targetLocation.y = y
					}
			}

			onProperties(message.instance)

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

	fun sendUiData() {
		val data = newNodeData()
		data.uiTranslation = BaseNodeProperties.UITranslation(targetLocation.x, targetLocation.y)
		sendProperties(data)
	}

	protected fun newNodeData(): BaseNodeProperties {
		val path = this::class.qualifiedName!!.split(".")
		val name = NodeUtil.getNodeName(path[path.size - 2])
		val version = NodeUtil.getNodeVersion(path[path.size - 2])
		return createNewNodeProperties(name, version)
	}
}
