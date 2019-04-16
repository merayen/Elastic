package net.merayen.elastic.ui.objects.contextmenu

import net.merayen.elastic.ui.Draw

class EmptyContextMenuItem : ContextMenuItem() {
	override fun onDraw(draw: Draw) {
		// Skips drawing of the menu
	}
}
