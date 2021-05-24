package net.merayen.elastic.uinodes.list.basemath

import net.merayen.elastic.backend.logicnodes.list.basemath.BaseMathProperties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.util.math.prettyNumber
import kotlin.math.max
import kotlin.math.pow
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class BaseMathUI : UINode() {
	abstract val symbol: String
	abstract val propertiesCls: KClass<out BaseMathProperties>

	private val symbolLabel = Label()

	private val portParameters = ArrayList<PortParameter>()

	override fun onInit() {
		super.onInit()
		titlebar.title = symbol

		symbolLabel.fontSize = 24f
		symbolLabel.text = symbol
		symbolLabel.align = Label.Align.CENTER
		symbolLabel.translation.x = 120f / 2
		symbolLabel.translation.y = 10f
		add(symbolLabel)

		layoutWidth = 120f
		layoutHeight = 80f

		updateLayout()
	}

	final override fun onCreatePort(port: UIPort) {
		if (port.name == "out") {
			port.translation.x = 120f
			port.translation.y = 20f
		} else if (port.name.startsWith("in")) {
			val number = port.name.substring(2).toInt()
			port.translation.y = number * 20f + 45f

			val valueSelector = PopupParameter1D()
			valueSelector.handler = object : PopupParameter1D.Handler {
				override fun onMove(value: Float) {
					send(
						propertiesCls.primaryConstructor!!.callBy(
							mapOf(
								propertiesCls.primaryConstructor!!.parameters.first {
									it.name == "portValues"
								} to portParameters.map {
									(it.notConnected as PopupParameter1D).value.pow(10) * 1000000f
								}
							)
						)
					)
				}

				override fun onChange(value: Float) {}

				override fun onLabel(value: Float) = prettyNumber(value.pow(10) * 1000000f, 3)
			}

			val portParameter = PortParameter(this, port, valueSelector, UIObject())
			portParameter.translation.x = 10f
			portParameter.translation.y = number * 20f + 37f
			add(portParameter)

			portParameters.add(portParameter)
			sortPortParameters()

			port.displayName = onPortDisplayName(port)

			updateLayout()

		} else error("Unknown port ${port.name}")
	}

	/**
	 * Override this to give custom display names of the ports
	 */
	open fun onPortDisplayName(port: UIPort): String {
		return port.name
	}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as BaseMathProperties
		properties.portValues?.apply {
			for ((i, value) in portParameters.withIndex()) {
				if (size <= i)
					break

				(value.notConnected as PopupParameter1D).value = (get(i) / 1000000f).pow(.1f)
			}
		}
	}

	override fun onRemovePort(port: UIPort) {
		if (port.name.startsWith("in")) {
			val index = port.name.substring(2).toInt()
			remove(portParameters.removeAt(index))
			sortPortParameters()
		}
	}

	override fun onData(message: NodeDataMessage) {}

	private fun updateLayout() {
		layoutHeight = getPorts().filter { it.name.startsWith("in") }.maxOf { it.translation.y } + 20f
	}

	private fun sortPortParameters() {
		portParameters.sortBy { it.port.name.substring(2).toInt() }
	}
}
