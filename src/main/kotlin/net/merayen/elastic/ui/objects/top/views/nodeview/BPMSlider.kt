package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.ParameterSlider
import kotlin.math.max
import kotlin.math.min

class BPMSlider : UIObject() {
	interface Handler {
		fun onChange(bpm: Int)
	}

	var handler: Handler? = null

	private var bpm = 120
	private var bpmSet = -1
	private val MIN = 30
	private val MAX = 300

	private val bpmSlider = ParameterSlider()

	override fun onInit() {
		bpmSlider.setHandler(object : ParameterSlider.IHandler {
			init {
				bpmSlider.value = (1/(MAX-MIN).toDouble())*120
				bpmSlider.scale = 1/5f
			}

			override fun onChange(value: Double, programatic: Boolean) {
				bpm = (MIN+(MAX-MIN) * value).toInt()
				if (bpmSet != bpm) {
					handler?.onChange(bpm)
					bpmSet = bpm
				}
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
	}

	fun setBPM(newBpm: Int) {
		bpm = max(MIN, min(newBpm, MAX))
		bpmSlider.value = 1/(MAX-MIN).toDouble() * bpm
	}

	override fun getWidth() = bpmSlider.getWidth()
	override fun getHeight() = bpmSlider.getHeight()
}