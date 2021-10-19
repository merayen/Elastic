package net.merayen.elastic.uinodes.list.wave_1

import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.InputSignalParameters
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import net.merayen.elastic.ui.objects.components.curvebox.ACSignalBezierCurveBox
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.pow

class UI : UINode(), INodeEditable {
	private var frequencyPortParameter: PortParameter? = null
	private lateinit var curve: ACSignalBezierCurveBox

	private val typeSelect: DropDown

	private val frequency: Float
		get() = ((frequencyPortParameter!!.notConnected as PopupParameter1D).value * 10).toDouble().pow(4.301029995663981)
			.toFloat()

	private class DropDownItem(val type: Properties.Type) : DropDown.Item(Label(type.name), TextContextMenuItem(type.name))

	init {
		layoutWidth = 200f
		layoutHeight = 170f

		titlebar.title = "Signal generator"

		typeSelect = DropDown(handler = object : DropDown.Handler {
			override fun onChange(selected: DropDown.Item) {
				selected as DropDownItem
				send(Properties(type = selected.type.name))
			}
		})

		for (x in Properties.Type.values())
			typeSelect.addMenuItem(DropDownItem(x))

		createBezierWave()
	}

	override fun onInit() {
		super.onInit()

		typeSelect.translation.x = 10f
		typeSelect.translation.y = 50f
		add(typeSelect)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		val port = getPort("output")
		if (port != null)
			port.translation.x = layoutWidth

		draw.setColor(0f, 0f, 0f)
		draw.fillRect(10f, 80f, layoutWidth - 20f, layoutHeight - 80f - 10f)
	}

	override fun onUpdate() {
		super.onUpdate()
		frequencyPortParameter?.layoutWidth = layoutWidth - 20f
	}

	override fun onProperties(properties: BaseNodeProperties) {
		if (properties is Properties) {
			val type = properties.type
			val frequencyData = properties.frequency
			val curveData = properties.curve

			if (type != null) {
				for (x in typeSelect.getItems()) {
					val item = x as DropDownItem
					if (item.type.name == type) {
						typeSelect.setViewItem(item)
						updateBezierWaveVisibility(item.type)
						break
					}
				}
			}

			if (frequencyData != null) {
				(frequencyPortParameter!!.notConnected as PopupParameter1D).value = (frequencyData.toDouble()
					.pow(1 / 4.301029995663981) / 10.0).toFloat()
			}

			if (curveData != null)
				curve.setPoints(curveData)

			// TODO should probably fix this...? What does it do? Why are we not just using frequency? Due to amplitude?
			//	instance.key == "data.InputSignalParameters:frequency" ->
			//		(frequency_port_parameter!!.connected as InputSignalParameters).handleMessage(instance)
			//}
		}
	}

	public override fun onCreatePort(port: UIPort) {
		if (port.name == "frequency") {
			val isp = InputSignalParameters(this, "frequency")
			isp.handler = InputSignalParameters.Handler { amplitude, offset ->
				send(
					Properties(
						inputAmplitude = amplitude,
						inputOffset = offset
					)
				)
			}
			frequencyPortParameter = PortParameter(this, getPort("frequency")!!, PopupParameter1D(), isp)
			frequencyPortParameter!!.translation.x = 10f
			frequencyPortParameter!!.translation.y = 20f
			add(frequencyPortParameter!!)

			(frequencyPortParameter!!.notConnected as PopupParameter1D).handler = object : PopupParameter1D.Handler {
				override fun onMove(value: Float) {
					send(Properties(frequency = frequency))
				}

				override fun onChange(value: Float) {}

				override fun onLabel(value: Float) = String.format("Frequency: %.3f", frequency)
			}

			(frequencyPortParameter!!.notConnected as PopupParameter1D).drag_scale = 0.5f

			port.translation.y = 20f
		}

		if (port.name == "out") {
			port.translation.x = layoutWidth
			port.translation.y = 20f
			port.color = UIPort.AUDIO_PORT
		}
	}

	private fun createBezierWave() {
		val bwb = ACSignalBezierCurveBox()
		bwb.translation.x = 10f
		bwb.translation.y = 80f
		bwb.layoutWidth = layoutWidth - 20f
		bwb.layoutHeight = layoutHeight - 80f - 10f
		curve = bwb

		bwb.handler = object : ACSignalBezierCurveBox.Handler {
			var i: Int = 0
			override fun onChange() {
				send(Properties(curve = bwb.floats))
			}

			override fun onMove() {
				if (i++ % 10 == 0) // FIXME Should really be based on time
					send(Properties(curve = bwb.floats))
			}

			override fun onDotClick() {}
		}
	}

	private fun updateBezierWaveVisibility(typeSelected: Properties.Type) {
		// This is so bullshitzy, but it works. Should rather page.
		if (typeSelected == Properties.Type.CURVE && curve.parent == null) {
			add(curve)
			layoutHeight = 170f
		} else if (typeSelected != Properties.Type.CURVE && curve.parent != null) {
			remove(curve)
			layoutHeight = 100f
		}
	}

	override fun onData(message: NodeDataMessage) {}
	override fun onRemovePort(port: UIPort) {}
	override fun getNodeEditor() = Editor(nodeId)
}
