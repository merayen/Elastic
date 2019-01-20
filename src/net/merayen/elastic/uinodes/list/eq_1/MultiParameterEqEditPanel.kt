package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.CircularSlider
import net.merayen.elastic.ui.objects.components.framework.Joystick

class MultiParameterEqEditPanel : UIObject() {
	private val algorithm = Joystick(object : Joystick.Handler {
		override fun onMove(x: Float, y: Float) {}
		override fun onDrop() {}
		override fun onLabel(x: Float, y: Float) = "LP16"
	})

	private val frequency = CircularSlider()
	private val amplitude = CircularSlider()

	override fun onInit() {
		algorithm.translation.y = 10f
		add(algorithm)

		frequency.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) {}
		}
		frequency.label.text = "Frequency"
		frequency.translation.x = 70f
		frequency.size = 20f
		add(frequency)

		amplitude.handler = object : CircularSlider.Handler {
			override fun onChange(value: Float) {
				amplitude.valueLabel
			}
		}
		amplitude.label.text = "Amplitude"
		amplitude.translation.x = 100f
		amplitude.size = 20f
		add(amplitude)
	}
}