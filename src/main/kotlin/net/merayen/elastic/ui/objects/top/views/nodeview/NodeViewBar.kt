package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar
import kotlin.math.log
import kotlin.math.max
import kotlin.math.pow

internal class NodeViewBar(private val nodeView: NodeView) : ViewBar(NodeView::class) {
	private val bpmSlider = ParameterSlider()
	private val monitorVolumeSlider = ParameterSlider()

	override fun onInit() {
		super.onInit()

		bpmSlider.setHandler(object : ParameterSlider.IHandler {
			private var bpm = 120
			private val MIN = 30
			private val MAX = 300

			init {
				bpmSlider.value = (1/(MAX-MIN).toDouble())*120
				bpmSlider.scale = 1/5f
			}

			override fun onChange(value: Double, programatic: Boolean) {
				bpm = (MIN+(MAX-MIN) * value).toInt()
			}

			override fun onButton(offset: Int) {
				if (offset > 0)
					bpm++
				else
					bpm--
				bpmSlider.value = 1/(MAX-MIN).toDouble() * (bpm - MIN)
			}

			override fun onLabelUpdate(value: Double) = "BPM: $bpm"
		})
		add(bpmSlider)


		monitorVolumeSlider.setHandler(object : ParameterSlider.IHandler {
			private var volume = 1.0

			override fun onChange(value: Double, programatic: Boolean) {
				volume = max(1/10.0.pow(12), value.pow(10))
			}

			override fun onButton(offset: Int) {

			}

			override fun onLabelUpdate(value: Double) = "%.0f dB".format(log(1/volume, 10.0) * 10)
		})
		add(monitorVolumeSlider)
	}
}