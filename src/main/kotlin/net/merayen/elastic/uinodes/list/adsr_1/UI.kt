package net.merayen.elastic.uinodes.list.adsr_1

import net.merayen.elastic.backend.logicnodes.list.adsr_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.components.Knob
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val clip: UIClip
	private val adsrgraph: ADSRGraph

	private val attack_slider: Knob
	private val decay_slider: Knob
	private val sustain_slider: Knob
	private val release_slider: Knob

	init {
		titlebar.title = "ADSR"

		clip = UIClip()
		clip.translation.x = 20f
		clip.translation.y = 20f
		clip.layoutWidth = 200f
		clip.layoutHeight = 150f
		add(clip)

		adsrgraph = ADSRGraph()
		adsrgraph.width = 160f
		adsrgraph.height = 150f
		clip.add(adsrgraph)

		var label = Label()
		label.text = "Attack"
		label.translation.x = 35f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		attack_slider = Knob()
		attack_slider.translation.x = 20f
		attack_slider.translation.y = 190f
		attack_slider.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				send(Properties(attack = Math.pow(value.toDouble(), 2.0).toFloat() * 10))
				adsrgraph.attack_time = Math.pow(attack_slider.value.toDouble(), 2.0).toFloat() * 10
			}
		}
		add(attack_slider)

		label = Label()
		label.text = "Decay"
		label.translation.x = 75f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		decay_slider = Knob()
		decay_slider.translation.x = 60f
		decay_slider.translation.y = 190f
		decay_slider.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				send(Properties(decay = Math.pow(value.toDouble(), 2.0).toFloat() * 10))
				adsrgraph.decay_time = Math.pow(decay_slider.value.toDouble(), 2.0).toFloat() * 10
			}
		}
		add(decay_slider)

		label = Label()
		label.text = "Sustain"
		label.translation.x = 115f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		sustain_slider = Knob()
		sustain_slider.translation.x = 100f
		sustain_slider.translation.y = 190f
		sustain_slider.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				send(Properties(sustain = value))
				adsrgraph.sustain_value = sustain_slider.value
			}
		}
		add(sustain_slider)

		label = Label()
		label.text = "Release"
		label.translation.x = 155f
		label.translation.y = 175f
		label.align = Label.Align.CENTER
		add(label)

		release_slider = Knob()
		release_slider.translation.x = 140f
		release_slider.translation.y = 190f
		release_slider.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				send(Properties(release = Math.pow(value.toDouble(), 2.0).toFloat() * 10))
				adsrgraph.release_time = Math.pow(release_slider.value.toDouble(), 2.0).toFloat() * 10
			}
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

	override fun onProperties(message: BaseNodeProperties) {
		val data = message as Properties
		val attack = data.attack
		val decay = data.decay
		val sustain = data.sustain
		val release = data.release

		if (attack != null) {
			attack_slider.value = Math.pow((attack / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.attack_time = attack
		} else if (decay != null) {
			decay_slider.value = Math.pow((decay.toFloat() / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.decay_time = decay
		} else if (sustain != null) {
			sustain_slider.value = sustain
			adsrgraph.sustain_value = sustain
		} else if (release != null) {
			release_slider.value = Math.pow((release / 10).toDouble(), (1 / 2f).toDouble()).toFloat()
			adsrgraph.release_time = release
		}
	}

	override fun onData(message: NodeDataMessage) {}
}
