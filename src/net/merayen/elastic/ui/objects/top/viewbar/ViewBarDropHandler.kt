package net.merayen.elastic.ui.objects.top.viewbar

import net.merayen.elastic.ui.objects.components.dragdrop.TargetItem
import net.merayen.elastic.ui.objects.top.mouse.MouseCarryItem

class ViewBarDropHandler(val target: ViewBar) : TargetItem(target) {
	override fun onInterest(item: MouseCarryItem) {}
	override fun onBlurInterest() {}
	override fun onHover(item: MouseCarryItem) {}
	override fun onBlur() {}
	override fun onDrop(item: MouseCarryItem) {}
}