package net.merayen.elastic.ui.objects.components.oscilloscope

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Checkbox
import net.merayen.elastic.ui.objects.components.Knob
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class Oscilloscope : UIObject(), FlexibleDimension {
	interface Handler {
		/**
		 * User has turned one of the knobs.
		 * Read their state and modify the data by their settings.
		 */
		fun onSettingChange()

		/**
		 * When user has clicked the Auto-checkbox and wants to change the mode
		 */
		fun onAutoChange(auto: Boolean)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var samples
		set(value) {
			signalDisplay.samples = value
		}
		get() = signalDisplay.samples

	var handler: Handler? = null

	var amplitude = 0f
		set(value) {
			field = max(0f, min(1000f, value))
			amplitudeKnob.value = (field / 1000f).pow(1 / 4f)
		}
		get() = amplitudeKnob.value.pow(4f) * 1000f

	var offset = 0f
		set(value) {
			field = max(-1000f, min(1000f, value))
			val ran = field / 1000f
			offsetKnob.value = (if (ran > 0) ran.pow(1 / 3f) else -(-ran).pow(1 / 3f)) / 2 + 0.5f
		}
		get() {
			val ran = (offsetKnob.value * 2 - 1)
			return if (ran > 0) ran.pow(3f) * 1000f else -(-ran).pow(3f) * 1000f
		}

	var time = 0f
		set(value) {
			field = max(0.000001f, min(1f, value))
			timeKnob.value = field.pow(1 / 3f)
		}
		get() = timeKnob.value.pow(3)

	var trigger = 0f
		set(value) {
			field = max(-1000f, min(1000f, value))
			val ran = field / 1000f
			triggerKnob.value = (if (ran > 0) ran.pow(1 / 3f) else -(-ran).pow(1 / 3f)) / 2 + 0.5f
		}
		get() {
			val ran = (triggerKnob.value * 2 - 1)
			return if (ran > 0) ran.pow(3f) * 1000f else -(-ran).pow(3f) * 1000f
		}

	var auto
		set(value) {
			autoCheckbox.checked = value
		}
		get() = autoCheckbox.checked

	private val signalDisplay = SignalDisplay()
	private val signalDisplayOverlay = SignalDisplayOverlay()
	private val amplitudeKnob = Knob()
	private val offsetKnob = Knob()
	private val timeKnob = Knob()
	private val triggerKnob = Knob()
	private val autoCheckbox = Checkbox()

	override fun onInit() {
		add(signalDisplay)
		add(signalDisplayOverlay)

		amplitudeKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				handler?.onSettingChange()
			}

			override fun onLabelUpdate(value: Float) = "${(amplitude * 10).roundToInt() / 10}x"
		}
		amplitudeKnob.translation.x = 0f
		amplitudeKnob.label.text = "Amplitude"
		add(amplitudeKnob)

		offsetKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				handler?.onSettingChange()
			}

			override fun onLabelUpdate(value: Float) = "${(offset * 10).roundToInt() / 10}"
		}
		offsetKnob.translation.x = 40f
		offsetKnob.label.text = "Offset"
		add(offsetKnob)

		timeKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				handler?.onSettingChange()
			}

			override fun onLabelUpdate(value: Float) = "${(time * 1000).roundToInt()}ms"
		}
		timeKnob.translation.x = 80f
		timeKnob.label.text = "Time/div"
		add(timeKnob)

		triggerKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) {
				handler?.onSettingChange()
			}

			override fun onLabelUpdate(value: Float) = "${(trigger * 10).roundToInt() / 10}"
		}
		triggerKnob.translation.x = 120f
		triggerKnob.label.text = "Trigger"
		add(triggerKnob)

		autoCheckbox.label.text = "Auto"
		autoCheckbox.translation.x = 160f
		autoCheckbox.whenChanged = {
			handler?.onAutoChange(autoCheckbox.checked)
		}
		add(autoCheckbox)
	}

	override fun onUpdate() {
		val knobY = layoutHeight - 20f
		signalDisplay.layoutWidth = layoutWidth
		signalDisplay.layoutHeight = knobY
		amplitudeKnob.translation.y = knobY
		offsetKnob.translation.y = knobY
		timeKnob.translation.y = knobY
		triggerKnob.translation.y = knobY
		autoCheckbox.translation.y = knobY

		signalDisplayOverlay.layoutWidth = layoutWidth
		signalDisplayOverlay.layoutHeight = knobY
		signalDisplayOverlay.time = time

		if (auto) {
			amplitudeKnob.disabled = true
			offsetKnob.disabled = true
			triggerKnob.disabled = true
		} else {
			amplitudeKnob.disabled = false
			offsetKnob.disabled = false
			triggerKnob.disabled = false
		}
	}
}
