package net.merayen.elastic.uinodes.list.signalgenerator_1

import net.merayen.elastic.backend.logicnodes.list.signalgenerator_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBoxControlFrame
import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor

class Editor(nodeId: String) : NodeEditor(nodeId) {
	private val layout = AutoLayout(LayoutMethods.HorizontalLiquidBox())
	private val curve = createBezierWave()

	override fun onInit() {
		super.onInit()
		add(layout)
		layout.add(curve)
		layout.placement.applyConstraint(curve, LayoutMethods.HorizontalLiquidBox.Constraint(0.5f))
		layout.placement.layoutWidth = 100f
		layout.placement.layoutHeight = 100f
		layout.add(object : UIObject() {
			override fun onDraw(draw: Draw) {
				draw.setColor(255, 255, 0)
				draw.setStroke(2f)
				draw.rect(0f, 0f, 50f, 50f)
			}

			override fun getWidth() = 50f
			override fun getHeight() = 50f
		})
	}

	override fun onUpdate() {
		super.onUpdate()
		layout.placement.layoutWidth = getWidth()
		layout.placement.layoutHeight = getHeight()
	}

	override fun onParameter(instance: BaseNodeData) {
		val data = instance as Data
		val curveData = data.curve

		if (curveData != null) {
			curve.bezier.setPoints(curveData)
		}

	}

	private fun createBezierWave(): SignalBezierCurveBoxControlFrame {
		val bwb = SignalBezierCurveBoxControlFrame()
		bwb.translation.x = 20f
		bwb.translation.y = 40f
		bwb.layoutWidth = 160f
		bwb.layoutHeight = 100f

		bwb.bezier.setHandler(object : SignalBezierCurveBox.Handler {
			internal var i: Int = 0
			override fun onChange() = send()

			override fun onMove() {
				if (i++ % 10 == 0) // FIXME Should really be based on time
					send()
			}

			override fun onDotClick() {}

			private fun send() = sendMessage(NodeParameterMessage(nodeId, Data(curve = bwb.bezier.floats)))
		})

		return bwb
	}

	override fun onMessage(message: NodeMessage) {}
}