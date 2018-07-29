package net.merayen.elastic.ui.objects.components.dragdrop

import net.merayen.elastic.ui.objects.components.PopupLabel
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

abstract class DragPopupLabel(val text: String) : MouseCarryItem() {
	private val popupLabel = PopupLabel(text)

	init {
		add(popupLabel)
	}

	override fun getWidth() = popupLabel.getWidth()
	override fun getHeight() = popupLabel.getHeight()
}