package net.merayen.elastic.uinodes.list.signalgenerator_1

import net.merayen.elastic.backend.logicnodes.list.signalgenerator_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.InputSignalParameters
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBoxControlFrame
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.pow

class UI : UINode(), INodeEditable {
	private var frequencyPortParameter: PortParameter? = null
	private lateinit var curve: SignalBezierCurveBoxControlFrame

	private val frequency: Float
		get() = ((frequencyPortParameter!!.notConnected as PopupParameter1D).value * 10).toDouble().pow(4.301029995663981)
			.toFloat()

	init {
		layoutWidth = 200f
		layoutHeight = 170f

		titlebar.title = "Signal generator"

		createBezierWave()
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		val port = getPort("output")
		if (port != null)
			port.translation.x = layoutWidth

		draw.setColor(0f, 0f, 0f)
		draw.fillRect(20f, 80f, 160f, 80f)
	}

	override fun onProperties(properties: BaseNodeProperties) {
		if (properties is Properties) {
			val frequencyData = properties.frequency
			val curveData = properties.curve

			if (frequencyData != null) {
				(frequencyPortParameter!!.notConnected as PopupParameter1D).value = (frequencyData.toDouble()
					.pow(1 / 4.301029995663981) / 10.0).toFloat()
			}

			if (curveData != null)
				curve.bezier.setPoints(curveData)

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
				sendProperties(
					Properties(
						inputAmplitude = amplitude,
						inputOffset = offset
					)
				)
			}
			frequencyPortParameter = PortParameter(this, getPort("frequency")!!, PopupParameter1D(), isp)
			frequencyPortParameter!!.translation.x = 20f
			frequencyPortParameter!!.translation.y = 20f
			add(frequencyPortParameter!!)

			(frequencyPortParameter!!.notConnected as PopupParameter1D).handler = object : PopupParameter1D.Handler {
				override fun onMove(value: Float) {
					sendProperties(Properties(frequency = frequency))
				}

				override fun onChange(value: Float) {}

				override fun onLabel(value: Float) = String.format("Frequency: %.3f", frequency)
			}

			(frequencyPortParameter!!.notConnected as PopupParameter1D).drag_scale = 0.5f

			port.translation.y = 20f
		}

		if (port.name == "output") {
			port.translation.y = 20f
			port.color = UIPort.AUDIO_PORT
		}
	}

	private fun createBezierWave() {
		val bwb = SignalBezierCurveBoxControlFrame()
		bwb.translation.x = 20f
		bwb.translation.y = 60f
		bwb.layoutWidth = 160f
		bwb.layoutHeight = 100f
		add(bwb)
		curve = bwb

		bwb.bezier.setHandler(object : SignalBezierCurveBox.Handler {
			var i: Int = 0
			override fun onChange() {
				sendProperties(Properties(curve = bwb.bezier.floats))
			}

			override fun onMove() {
				if (i++ % 10 == 0) // FIXME Should really be based on time
					sendProperties(Properties(curve = bwb.bezier.floats))
			}

			override fun onDotClick() {}
		})
	}

	override fun onData(message: NodeDataMessage) {}
	override fun onRemovePort(port: UIPort) {}
	override fun getNodeEditor() = Editor(nodeId)
}
