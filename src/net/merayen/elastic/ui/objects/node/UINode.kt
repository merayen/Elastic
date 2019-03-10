package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.system.intercom.*
import java.util.ArrayList

import net.merayen.elastic.ui.Draw

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView

abstract class UINode : UIObject(), FlexibleDimension {
	lateinit var nodeId: String // Same ID as in the backend-system, netlist etc
	override var layoutWidth = 100f
	override var layoutHeight = 80f

	protected var titlebar: Titlebar
		private set

	private val nodePorts = ArrayList<UIPort>()

	protected val ports
		get() = ArrayList<UIPort>(nodePorts)

	private var inited: Boolean = false

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
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		/*draw.setColor(100, 100, 100)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(180, 180, 180)
		draw.fillRect(1f, 1f, layoutWidth - 2f, layoutHeight - 2f)*/

		/*draw.setColor(100, 100, 100)
		draw.fillRect(2f, 2f, layoutWidth - 4f, layoutHeight - 4f)*/

		titlebar.layoutWidth = layoutWidth

		super.onDraw(draw)
	}

	fun getPort(name: String): UIPort? {
		for (x in nodePorts)
			if (x.name == name)
				return x

		return null
	}

	fun sendParameter(key: String, value: Any) {
		sendMessage(NodeParameterMessage(nodeId, key, value))
	}

	fun sendData(value: Map<String, Any>) {
		sendMessage(NodeDataMessage(nodeId, value))
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
		} else if (message is NodeStatusMessage)
			println(message)
	}
}
