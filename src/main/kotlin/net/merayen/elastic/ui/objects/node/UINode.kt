package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.*
import java.util.ArrayList

import net.merayen.elastic.ui.Draw

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView

abstract class UINode : UIObject(), FlexibleDimension {
	lateinit var nodeId: String // Same ID as in the backend-system, netlist etc
	override var layoutWidth = 100f
	override var layoutHeight = 80f

	protected lateinit var titlebar: Titlebar
		private set

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
		titlebar = Titlebar()
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

						if (newTranslation != null) {
							translation.x = translation.x
							translation.y = translation.y
						}
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
			println(message) // TODO implement
	}

	private fun sendUiData() {
		sendParameter(
				BaseNodeData(
						uiTranslation = BaseNodeData.UITranslation(translation.x, translation.y)
				)
		)
	}
}
