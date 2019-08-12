package net.merayen.elastic.uinodes.list.metronome_1

import net.merayen.elastic.backend.logicnodes.list.metronome_1.MetronomeBeatMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class BeatIndicator : UIObject() { // TODO take it out, make available for others
	private var lastFlash = 0L
	private var lastFlashSignal = 0
	private var barDivision = 0

	override fun onDraw(draw: Draw) {
		for (i in 0 until barDivision) {
			draw.setColor(0,0,0)
			draw.setStroke(1.2f)
			draw.oval(10f * i, 0f, 8f, 8f)

			if (lastFlash + 100 > System.currentTimeMillis() && lastFlashSignal == i) {
				draw.setColor(0.5f, 1.0f, 0.5f)
				draw.fillOval(10f * i, 0f, 8f, 8f)
			}
		}
	}

	fun handleMessage(message: MetronomeBeatMessage) {
		lastFlash = System.currentTimeMillis()
		lastFlashSignal = message.current
		barDivision = message.division
	}
}