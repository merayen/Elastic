package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

class EditNodeMouseCarryItem(val node: UINode) : MouseCarryItem() {

	override fun onDraw(draw: Draw) {
		draw.setColor(255, 255, 255)
		draw.fillRect(0f, 0f, 150f, 20f)

		draw.setColor(50, 50, 50)
		draw.setFont("", 16f)
		draw.text("Edit node", 10f, 16f)
	}
}