package net.merayen.elastic.ui.objects.top.mouse

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject

class InterestedObjectDecoration(private val target: UIObject) : UIObject() {
	override fun onDraw(draw: Draw) {
		val w = target.getWidth()
		val h = target.getHeight()
		draw.setColor(255,0,255)
		draw.fillRect(0f, 0f, w, h)
	}
}