package net.merayen.elastic.uinodes.list.signalgenerator_1

import net.merayen.elastic.backend.logicnodes.list.signalgenerator_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
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

class UI : UINode(), INodeEditable {
	private var frequency_port_parameter: PortParameter? = null
	private lateinit var curve: SignalBezierCurveBoxControlFrame

	private val frequency: Float
		get() = Math.pow(((frequency_port_parameter!!.not_connected as PopupParameter1D).value * 10).toDouble(), 4.301029995663981).toFloat()

	init {
		layoutWidth = 200f
		layoutHeight = 150f

		titlebar.title = "Signal generator"

		createBezierWave()
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		val port = getPort("output")
		if (port != null)
			port.translation.x = layoutWidth
	}

	override fun onMessage(instance: BaseNodeData) {
		if (instance is Data) {
			val frequencyData = instance.frequency
			val curveData = instance.curve

			if (frequencyData != null) {
				(frequency_port_parameter!!.not_connected as PopupParameter1D).value = (Math.pow(frequencyData.toDouble(), 1 / 4.301029995663981) / 10.0).toFloat()
				updateFrequencyText()
			}

			if (curveData != null) {
				curve.bezier.setPoints(curveData)
			}


			// TODO should probably fix this...? What does it do? Why are we not just using frequency? Due to amplitude?
			//	instance.key == "data.InputSignalParameters:frequency" ->
			//		(frequency_port_parameter!!.connected as InputSignalParameters).handleMessage(instance)
			//}
		}
	}

	public override fun onCreatePort(port: UIPort) {
		if (port.name == "frequency") {
			val isp = InputSignalParameters(this, "frequency")
			isp.handler = InputSignalParameters.Handler { amplitude, offset -> sendParameter(Data(inputAmplitude = amplitude, inputOffset = offset)) }
			frequency_port_parameter = PortParameter(this, getPort("frequency")!!, PopupParameter1D(), isp)
			frequency_port_parameter!!.translation.x = 20f
			frequency_port_parameter!!.translation.y = 20f
			add(frequency_port_parameter!!)

			(frequency_port_parameter!!.not_connected as PopupParameter1D).setHandler(object : PopupParameter1D.Handler {
				override fun onMove(value: Float) {
					updateFrequencyText()
					sendParameter(Data(frequency = frequency))
				}

				override fun onChange(value: Float) {}
			})

			(frequency_port_parameter!!.not_connected as PopupParameter1D).drag_scale = 0.5f

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
		bwb.translation.y = 40f
		bwb.layoutWidth = 160f
		bwb.layoutHeight = 100f
		add(bwb)
		curve = bwb

		bwb.bezier.setHandler(object : SignalBezierCurveBox.Handler {
			var i: Int = 0
			override fun onChange() {
				sendParameter(Data(curve = bwb.bezier.floats))
				//sendParameter("data.curve", bwb.bezier.floats)
			}

			override fun onMove() {
				if (i++ % 10 == 0) // FIXME Should really be based on time
					sendParameter(Data(curve = bwb.bezier.floats))
			}

			override fun onDotClick() {}
		})
	}

	override fun onData(message: NodeDataMessage) {}

	override fun onRemovePort(port: UIPort) {}

	private fun updateFrequencyText() {
		(frequency_port_parameter!!.not_connected as PopupParameter1D).label.text = String.format("Frequency: %.3f", frequency)
	}

	override fun onParameter(instance: BaseNodeData) {}

	override fun getNodeEditor() = Editor(nodeId)
}
