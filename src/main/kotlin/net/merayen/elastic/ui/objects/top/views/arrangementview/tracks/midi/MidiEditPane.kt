package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.BaseEditPane
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.EventZone

class MidiEditPane : BaseEditPane() {

	var eventZone: EventZone? = null
		set(value) {
			if (field != null)
				; // Remove last one

			if (value != null)
				; // Start editing the next one

			field = value
		}


	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(1f, 0f, 1f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)

		draw.setColor(0f, 1f, 0f)
		draw.setFont("",32f)
		draw.text("Editing eventZone $eventZone.id", 0f, 32f)
	}
}