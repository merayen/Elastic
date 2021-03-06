package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Knob
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem

class MultiParameterEqEditPanel : UIObject() {
	/*private val algorithm = Joystick(object : Joystick.Handler {
		override fun onMove(x: Float, y: Float) {}
		override fun onDrop() {}
		override fun onLabel(x: Float, y: Float) = "LP16"
	})*/

	private val algorithms = arrayOf(
			"lp16" to "LP16",
			"lp24" to "LP24",
			"hp16" to "HP16"
	)

	private val algorithm = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {}
	})

	private val frequency = Knob()
	private val amplitude = Knob()

	override fun onInit() {
		for ( (name,_) in algorithms)
			algorithm.addMenuItem(DropDown.Item(Label(name), TextContextMenuItem(name)))

		algorithm.translation.y = 10f
		algorithm.layoutWidth = 50f
		add(algorithm)

		frequency.handler = object : Knob.Handler {
			override fun onChange(value: Float) {}
		}
		frequency.label.text = "Frequency"
		frequency.translation.x = 70f
		frequency.size = 20f
		add(frequency)

		amplitude.handler = object : Knob.Handler {
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