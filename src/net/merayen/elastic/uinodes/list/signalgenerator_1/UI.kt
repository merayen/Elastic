package net.merayen.elastic.uinodes.list.signalgenerator_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.InputSignalParameters
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBoxControlFrame
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private var frequency_port_parameter: PortParameter? = null
	private var curve: SignalBezierCurveBoxControlFrame? = null

	private//return (float)Math.pow(((PopupParameter1D)frequency_port_parameter.not_connected).getValue() * 2, 14);
	val frequency: Float
		get() = Math.pow(((frequency_port_parameter!!.not_connected as PopupParameter1D).value * 10).toDouble(), 4.301029995663981).toFloat()

	init {
		layoutWidth = 200f
		layoutHeight = 150f

		titlebar.title = "Signal generator"

		createBezierWave()
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		if (getPort("output") != null)
			getPort("output")!!.translation.x = layoutWidth
	}

	override fun onMessage(message: NodeParameterMessage) {
		if (message.key == "data.frequency") {
			(frequency_port_parameter!!.not_connected as PopupParameter1D).value = (Math.pow((message.value as Number).toFloat().toDouble(), 1 / 4.301029995663981) / 10.0).toFloat()
			updateFrequencyText()
		} else if (message.key == "data.curve") {
			curve!!.bezier.setPoints(message.value as List<Number>)
		} else if (message.key == "data.InputSignalParameters:frequency") {
			(frequency_port_parameter!!.connected as InputSignalParameters).handleMessage(message)
		}
	}

	public override fun onCreatePort(port: UIPort) {
		if (port.name == "frequency") {
			frequency_port_parameter = PortParameter(this, getPort("frequency"), PopupParameter1D(), InputSignalParameters(this, "frequency"))
			frequency_port_parameter!!.translation.x = 20f
			frequency_port_parameter!!.translation.y = 20f
			add(frequency_port_parameter!!)

			(frequency_port_parameter!!.not_connected as PopupParameter1D).setHandler(object : PopupParameter1D.Handler {
				override fun onMove(value: Float) {
					updateFrequencyText()
					sendParameter("data.frequency", frequency)
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
		bwb.width = 160f
		bwb.height = 100f
		add(bwb)
		curve = bwb

		bwb.bezier.setHandler(object : SignalBezierCurveBox.Handler {
			internal var i: Int = 0
			override fun onChange() {
				sendParameter("data.curve", bwb.bezier.floats)
			}

			override fun onMove() {
				if (i++ % 10 == 0)
					sendParameter("data.curve", bwb.bezier.floats)
			}

			override fun onDotClick() {

			}
		})
	}

	override fun onData(message: NodeDataMessage) {}

	override fun onRemovePort(port: UIPort) {}

	private fun updateFrequencyText() {
		(frequency_port_parameter!!.not_connected as PopupParameter1D).label.text = String.format("Frequency: %.3f", frequency)
	}

	override fun onParameter(key: String, value: Any) {}
}
