package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.CircularSlider

class SampleWaveBox : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val startKnob = CircularSlider()
	private val lengthKnob = CircularSlider()

	private val deleteZoneButton = Button()

	private var selectedZone: SampleWaveZone? = null

	private val sampleWaveform = SampleWaveform()
	private val sampleWaveBoxZones = SampleWaveBoxZones(object : SampleWaveBoxZones.Handler {
		override fun onZoneChange(zone: SampleWaveZone) {
			startKnob.value = zone.start
			lengthKnob.value = zone.stop
		}

		override fun onZoneSelect(zone: SampleWaveZone?) {
			selectedZone = zone
			if(zone != null) {
				startKnob.value = zone.start
				lengthKnob.value = zone.stop
			}
		}
	})

	override fun onInit() {
		add(sampleWaveform)
		add(sampleWaveBoxZones)

		startKnob.size = 20f
		lengthKnob.size = 20f

		startKnob.label.text = "Start"
		lengthKnob.label.text = "Length"

		deleteZoneButton.label = "Remove zone"

		startKnob.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) {
				val selectedZone = selectedZone
				if(selectedZone != null)
					selectedZone.start = value
			}
		}

		lengthKnob.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) {
				val selectedZone = selectedZone
				if(selectedZone != null)
					selectedZone.stop = value
			}
		}

		deleteZoneButton.handler = object : Button.IHandler {
			override fun onClick() {
				val zone = selectedZone
				if(zone != null) {
					sampleWaveBoxZones.removeZone(zone)
					selectedZone = null
				}
			}
		}
	}

	override fun onUpdate() {
		sampleWaveform.layoutWidth = layoutWidth
		sampleWaveform.layoutHeight = layoutHeight - 30

		sampleWaveBoxZones.layoutWidth = layoutWidth
		sampleWaveBoxZones.layoutHeight = layoutHeight - 30

		if(selectedZone != null) {
			if(startKnob.parent == null) {
				add(startKnob)
				add(lengthKnob)
				add(deleteZoneButton)
			}

			startKnob.translation.x = 10f
			startKnob.translation.y = layoutHeight - 20
			lengthKnob.translation.x = 40f
			lengthKnob.translation.y = layoutHeight - 20
			deleteZoneButton.translation.x = 70f
			deleteZoneButton.translation.y = layoutHeight - 20
		} else if(startKnob.parent != null) {
			remove(startKnob)
			remove(lengthKnob)
			remove(deleteZoneButton)
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.1f, 0.1f, 0f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight - 29)

		draw.setColor(150, 150, 150)
		draw.setStroke(1f)
		draw.line(0f, (layoutHeight - 30) / 2f, layoutWidth, (layoutHeight - 30) / 2f)
	}

	fun applyWaveform(waveForm: FloatArray) {
		sampleWaveform.waveForm = waveForm
	}
}