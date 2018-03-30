package net.merayen.elastic.uinodes.list.adsr_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.components.CircularSlider
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val clip: UIClip
	private val adsrgraph: ADSRGraph

	private val attack_slider: CircularSlider
	private val decay_slider: CircularSlider
	private val sustain_slider: CircularSlider
	private val release_slider: CircularSlider

	init {
		titlebar.title = "ADSR"

		clip = UIClip()
		clip.translation.x = 20f
		clip.translation.y = 20f
		clip.width = 200f
		clip.height = 150f
		add(clip)

		adsrgraph = ADSRGraph()
		adsrgraph.width = 160f
		adsrgraph.height = 150f
		clip.add(adsrgraph)

		var label = Label()
		label.label = "Attack"
		label.translation.x = 35f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		attack_slider = CircularSlider()
		attack_slider.translation.x = 20f
		attack_slider.translation.y = 190f
		attack_slider.setHandler { v ->
			sendParameter("attack", Math.pow(v.toDouble(), 2.0).toFloat() * 10)
			adsrgraph.attack_time = Math.pow(attack_slider.value.toDouble(), 2.0).toFloat() * 10
		}
		add(attack_slider)

		label = Label()
		label.label = "Decay"
		label.translation.x = 75f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		decay_slider = CircularSlider()
		decay_slider.translation.x = 60f
		decay_slider.translation.y = 190f
		decay_slider.setHandler { v ->
			sendParameter("decay", Math.pow(v.toDouble(), 2.0).toFloat() * 10)
			adsrgraph.decay_time = Math.pow(decay_slider.value.toDouble(), 2.0).toFloat() * 10
		}
		add(decay_slider)

		label = Label()
		label.label = "Sustain"
		label.translation.x = 115f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		sustain_slider = CircularSlider()
		sustain_slider.translation.x = 100f
		sustain_slider.translation.y = 190f
		sustain_slider.setHandler { v ->
			sendParameter("sustain", v)
			adsrgraph.sustain_value = sustain_slider.value
		}
		add(sustain_slider)

		label = Label()
		label.label = "Release"
		label.translation.x = 155f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		release_slider = CircularSlider()
		release_slider.translation.x = 140f
		release_slider.translation.y = 190f
		release_slider.setHandler { v ->
			sendParameter("release", Math.pow(v.toDouble(), 2.0).toFloat() * 10)
			adsrgraph.release_time = Math.pow(release_slider.value.toDouble(), 2.0).toFloat() * 10
		}
		add(release_slider)

		attack_slider.value = 0.1f
		decay_slider.value = 0.1f
		sustain_slider.value = 1f
		release_slider.value = 0.1f
	}

	override fun onInit() {
		super.onInit()
		layoutWidth = 200f
		layoutHeight = 240f
	}

	public override fun onCreatePort(port: UIPort) {
		if (port.name == "input") {
			port.translation.x = 0f
			port.translation.y = 20f
			port.color = UIPort.MIDI_PORT
		}

		if (port.name == "output") {
			port.translation.x = 200f
			port.translation.y = 20f
			port.color = UIPort.MIDI_PORT
		}

		/*if(port.name.equals("fac")) {
			port.translation.x = layoutWidth;
			port.translation.y = layoutHeight - 20;
			port.color = UIPort.AUX_PORT;
		}*/
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {
		if (message.key == "attack") {
			attack_slider.value = Math.pow(((message.value as Number).toFloat() / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.attack_time = message.value.toFloat()
		} else if (message.key == "decay") {
			decay_slider.value = Math.pow(((message.value as Number).toFloat() / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.decay_time = message.value.toFloat()
		} else if (message.key == "sustain") {
			sustain_slider.value = (message.value as Number).toFloat()
			adsrgraph.sustain_value = message.value.toFloat()
		} else if (message.key == "release") {
			release_slider.value = Math.pow(((message.value as Number).toFloat() / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.release_time = message.value.toFloat()
		}
	}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {}
}
