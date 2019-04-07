package net.merayen.elastic.ui.objects.top.mouse

import net.merayen.elastic.ui.UIObject

abstract class MouseCarryItem : UIObject() {
	/**
	 * A MouseCarryItem should not implement event support
	 */
	fun onEvent() {}
}