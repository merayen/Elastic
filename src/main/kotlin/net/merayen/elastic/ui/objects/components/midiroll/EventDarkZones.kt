package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

/**
 * Draws over and disables parts of the score that has no event zones.
 * Makes so that the user can not click on it.
 * TODO allow adding event zone in those dark zones?
 */
internal class EventDarkZones : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private class Zone : UIObject(), FlexibleDimension {
		override var layoutWidth = 0f
		override var layoutHeight = 0f

		override fun onDraw(draw: Draw) {
			draw.setColor(0f, 0f, 0f, 0.1f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}

	}

	var beatWidth = 0f

	private val zones = ArrayList<Zone>()

	fun applyDarkZones(startStops: ArrayList<StartStop>) {
		for (i in 0 until zones.size - startStops.size)
			zones.add(Zone())

		for (i in 0 until zones.size - startStops.size)
			zones.removeAt(zones.size-1)

		for (i in 0 until zones.size) {
			val startStop = startStops[i]

			zones[i].translation.x = startStop.start * beatWidth
			zones[i].layoutWidth = startStop.length * beatWidth
		}
	}
}