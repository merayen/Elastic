package net.merayen.elastic.ui.objects.top.views.editview

import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.EditNodeController
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.View

class EditNodeView : View() {
	private var editNodeController: EditNodeController? = null
	private var nodeEditor: NodeEditor? = null

	var nodeId: String? = null
		private set

	private val bar = EditNodeViewBar()
	private val content = UIObject()

	override fun cloneView(): View {
		val editNodeView = EditNodeView()
		editNodeView.nodeId = nodeId
		return editNodeView
	}

	override fun onInit() {
		super.onInit()
		sendMessage(EditNodeController.Hello(this))
		add(content)
		add(bar)

		content.translation.y = 40f
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		val nodeId = nodeId
		if(nodeId == null) {
			draw.setColor(50,50,50)
			draw.setFont("", 32f)
			val text = "(Nothing to edit currently)"
			draw.text(text, getWidth() / 2f - draw.getTextWidth(text) / 2f, getHeight() / 2f)
		}
	}

	override fun onUpdate() {
		super.onUpdate()
		nodeEditor?.layoutWidth = getWidth()
		nodeEditor?.layoutHeight = getHeight() - content.translation.y
		bar.layoutWidth = getWidth()
	}

	fun init(editNodeController: EditNodeController) {
		this.editNodeController = editNodeController
		if(nodeId != null)
			tryLoad()
	}

	fun receiveMessage(message: NodeMessage) {
		nodeEditor?.onMessage(message)

		if (message is NodePropertyMessage) {
			nodeEditor?.onParameter(message.instance)
		}
	}

	fun editNode(node: INodeEditable) {
		val nodeEditor = node.getNodeEditor()
		this.nodeEditor = nodeEditor
		nodeId = nodeEditor.nodeId

		content.removeAll()
		content.add(nodeEditor)

		tryLoad()
	}

	private fun tryLoad() {
		val nodeId = this.nodeId
		if (nodeId != null) {
			editNodeController?.getMessages(nodeId)?.forEach {
				receiveMessage(it as NodeMessage)
			}
		}
	}

	override val easyMotionBranch = object : Branch(this) {}
}