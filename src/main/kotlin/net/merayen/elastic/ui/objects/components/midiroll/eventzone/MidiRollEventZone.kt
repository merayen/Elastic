package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

internal class MidiRollEventZone(val id: String) : UIObject(), FlexibleDimension {
	/**
	 * Gets set by us automatically. Do only set layoutHeight.
	 */
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var start = 0f
	var length = 0f
	var beatWidth = 0f

	override fun onUpdate() {
		layoutWidth = beatWidth * length
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(1f, 0f, 0f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}
}