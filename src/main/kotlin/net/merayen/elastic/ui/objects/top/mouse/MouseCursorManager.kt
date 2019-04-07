package net.merayen.elastic.ui.objects.top.mouse

import net.merayen.elastic.ui.UIObject

/**
 * Manages mouse cursor over several surfaces (multiple windows)
 * */
class MouseCursorManager : UIObject() {
	private val surfaces = ArrayList<SurfaceMouseCursors>()
	private val carries = HashMap<Int, MouseCarryItem>()

	fun addSurface(surfaceMouseCursor: SurfaceMouseCursors) = surfaces.add(surfaceMouseCursor)

	fun setCarryItem(cursorId: Int, carryItem: MouseCarryItem) {
		carries[cursorId] = carryItem
	}

	fun removeCarryItem(cursorId: Int) {
		carries.remove(cursorId)
	}

	fun retrieveCarryItem(cursorId: Int): MouseCarryItem? {
		return carries[cursorId]
	}
}