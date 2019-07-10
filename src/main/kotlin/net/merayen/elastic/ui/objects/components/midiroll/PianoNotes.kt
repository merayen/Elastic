package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class PianoNotes(private val octaveCount: Int) : UIObject(), FlexibleDimension {
	class Note : UIObject(), FlexibleDimension {
		override var layoutWidth = 0f
		override var layoutHeight = 0f

		var selected = false

		override fun onDraw(draw: Draw) {
			if (selected)
				draw.setColor(1f, 0.5f, 0.5f)
			else
				draw.setColor(1f, 0.2f, 0.2f)

			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private val noteContainer = UIObject()

	var beatWidth = 10f
	var octaveWidth = 5f

	override fun onInit() {
		add(noteContainer)
	}

	override fun onUpdate() {
		// TODO rearrange notes based on beatWidth and tangentHeight
	}
}
