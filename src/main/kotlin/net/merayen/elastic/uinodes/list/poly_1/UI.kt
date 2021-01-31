package net.merayen.elastic.uinodes.list.poly_1

import net.merayen.elastic.backend.logicnodes.list.poly_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import kotlin.math.roundToInt

class UI : UINode(), EasyMotionBranch {
	private val unison: ParameterSlider

	init {
		layoutWidth = 100f
		layoutHeight = 70f

		val button = Button()
		button.label = "Open"
		button.translation.x = 10f
		button.translation.y = 20f
		button.handler = object : Button.IHandler {
			override fun onClick() {
				enter()
			}
		}
		add(button)

		unison = ParameterSlider()
		unison.translation.x = 5f
		unison.translation.y = 40f
		unison.setHandler(object : ParameterSlider.Handler {
			override fun onLabelUpdate(value: Double) = String.format("%d", (value * 31).roundToInt() + 1)

			override fun onChange(value: Double, programatic: Boolean) = sendProperties(Properties(unison = (value * 31).roundToInt() + 1))

			override fun onButton(offset: Int) {
				unison.value = unison.value + offset / 31.0
			}
		})
		add(unison)

		this.titlebar.title = "Poly"
	}

	override fun onInit() {
		super.onInit()

		easyMotionBranch.controls[setOf(KeyboardEvent.Keys.Q)] = Branch.Control {
			Branch.Control.STEP_BACK
		}

		easyMotionBranch.controls[setOf(KeyboardEvent.Keys.E)] = Branch.Control {
			enter()
			null
		}
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "input") {
			port.translation.y = 20f
		}

		if (port.name == "output") {
			port.translation.x = 100f
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onProperties(properties: BaseNodeProperties) {
		if (properties is Properties) {
			val unisonData = properties.unison
			if (unisonData != null)
				unison.value = (unisonData - 1) / 31.0
		}
	}

	private fun enter() {
		search.parentByType(NodeView::class.java)!!.swapView(nodeId)
	}
}
