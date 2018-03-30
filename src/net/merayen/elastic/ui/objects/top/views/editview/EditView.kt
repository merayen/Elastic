package net.merayen.elastic.ui.objects.top.views.editview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.views.View

class EditView : View() {
	override fun cloneView() = EditView()

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(0,0,0)
		draw.setFont("", 32f)
		draw.text("Select something", getWidth() / 2 - draw.getTextWidth("Select something") / 2f, getHeight() / 2f)
	}
}