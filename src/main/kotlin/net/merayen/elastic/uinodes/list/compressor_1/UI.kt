package net.merayen.elastic.uinodes.list.compressor_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.CircularSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.*

class UI : UINode() {
	private val WIDTH = 300f
	private val HEIGHT = 100f
	private val vu = VUMeter()
	private val attack = CircularSlider()
	private val release = CircularSlider()
	private val ratio = CircularSlider()
	private val knee = CircularSlider()
	private val threshold = CircularSlider()
	private val inputAmplitude = CircularSlider()
	private val inputSidechainAmplitude = CircularSlider()
	private val outputAmplitude = CircularSlider()

	private var compressionValue = 0f

	private var lastUpdate = 0L

	init {
		layoutWidth = WIDTH
		layoutHeight = HEIGHT

		inputAmplitude.translation.x = 10f
		inputAmplitude.translation.y = 30f
		inputAmplitude.size = 20f
		inputAmplitude.label.text = "Input amp"
		add(inputAmplitude)

		inputSidechainAmplitude.translation.x = 10f
		inputSidechainAmplitude.translation.y = 60f
		inputSidechainAmplitude.size = 20f
		inputSidechainAmplitude.label.text = "Sidechain amp"
		add(inputSidechainAmplitude)

		outputAmplitude.translation.x = 270f
		outputAmplitude.translation.y = 30f
		outputAmplitude.size = 20f
		outputAmplitude.label.text = "Output amp"
		add(outputAmplitude)

		vu.translation.x = 165f
		vu.translation.y = 20f
		vu.layoutWidth = 100f
		add(vu)

		attack.translation.x = 50f
		attack.translation.y = 20f
		attack.size = 25f
		attack.label.text = "Attack"
		add(attack)

		release.translation.x = 90f
		release.translation.y = 20f
		release.size = 25f
		release.label.text = "Release"
		add(release)

		ratio.translation.x = 130f
		ratio.translation.y = 20f
		ratio.size = 25f
		ratio.label.text = "Ratio"
		add(ratio)

		knee.translation.x = 50f
		knee.translation.y = 60f
		knee.size = 25f
		knee.label.text = "Knee"
		add(knee)

		threshold.translation.x = 90f
		threshold.translation.y = 60f
		threshold.size = 25f
		threshold.label.text = "Threshold"
		add(threshold)

		inputAmplitude.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("inputAmplitude", calcCentricPow(value))
			override fun onLabelUpdate(value: Float) = "${(log(abs(calcCentricPow(value)), 10f) * 10).roundToInt()}dB"
		}

		inputSidechainAmplitude.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("inputSidechainAmplitude", calcCentricPow(value))
			override fun onLabelUpdate(value: Float) = "${(log(abs(calcCentricPow(value)), 10f) * 10).roundToInt()}dB"
		}

		outputAmplitude.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("outputAmplitude", calcCentricPow(value))
			override fun onLabelUpdate(value: Float) = "${(log(abs(calcCentricPow(value)), 10f) * 10).roundToInt()}dB"
		}

		attack.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("attack", value.pow(2))
			override fun onLabelUpdate(value: Float) = "${(value * 1000).toInt()}ms"
		}

		release.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("release", value.pow(2))
			override fun onLabelUpdate(value: Float) = "${(value * 1000).toInt()}ms"
		}

		ratio.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("ratio", 1 + value * 9)
		}

		knee.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("knee", value)
		}

		threshold.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) = sendParameter("threshold", min(1f, value.pow(2) + 0.0001f))
			override fun onLabelUpdate(value: Float) = "-${(log(1 / min(1f, value.pow(2) + 0.0001f), 10f) * 10).roundToInt()}dB"
		}

		titlebar.title = "Compressor"
	}

	private fun calcCentricPow(value: Float) = 10f.pow((value * 2 - 1) * 3)
	private fun calcCentricToKnob(value: Float) = (log(max(0.001f, (value as Number).toFloat()), 10f) / 3 + 1) / 2

	override fun onUpdate() {
		super.onUpdate()

		val timeDelta = (System.currentTimeMillis() - lastUpdate) / 1000f

		vu.value += (compressionValue - vu.value) * Math.min(1f, timeDelta * 10)

		lastUpdate = System.currentTimeMillis()
	}

	override fun onCreatePort(port: UIPort) {
		when {
			port.name == "input" -> port.translation.y = 40f
			port.name == "sidechain" -> port.translation.y = 70f
			port.name == "output" -> { port.translation.x = WIDTH; port.translation.y = 40f }
			port.name == "attenuation" -> { port.translation.x = WIDTH; port.translation.y = 70f }
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {
		if(message.value["amplitude"] != null)
			compressionValue = 1 - log(1 / max(0.0001f, message.value["amplitude"] as Float), 10f) / 3f
	}

	override fun onParameter(key: String, value: Any) {
		when (key) {
			"attack" -> attack.value = (value as Number).toFloat().pow(0.5f)
			"release" -> release.value = (value as Number).toFloat().pow(0.5f)
			"threshold" -> threshold.value = ((value as Number).toFloat()).pow(0.5f)
			"inputAmplitude" -> inputAmplitude.value = calcCentricToKnob((value as Number).toFloat())
			"inputSidechainAmplitude" -> inputSidechainAmplitude.value = calcCentricToKnob((value as Number).toFloat())
			"outputAmplitude" -> outputAmplitude.value = calcCentricToKnob((value as Number).toFloat())
		}
	}
}