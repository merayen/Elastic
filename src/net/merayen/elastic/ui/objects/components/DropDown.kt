package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent.Button
import net.merayen.elastic.ui.objects.top.menu.MenuList
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class DropDown : UIObject(), FlexibleDimension {
	interface Handler {
		fun onClick()
	}

	override var layoutWidth = 100f
	override var layoutHeight = 20f

	private var mouse_handler: MouseHandler? = null
	private var menuList = MenuList()

	override fun onInit() {
		menuList.setHandler(object : MenuList.Handler() {
			override fun onOutsideClick() {
				if (menuList.parent != null)
					toggle()
			}
		})
		mouse_handler = MouseHandler(this, Button.LEFT)
		mouse_handler!!.setHandler(object : MouseHandler.Handler() {
			override fun onMouseDown(position: Point) {
				toggle()
			}

			override fun onMouseOutsideDown(global_position: Point) {}
		})
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)

		draw.rect(layoutWidth - 20, 0f, 20f, layoutHeight)
	}

	override fun onUpdate() {
		menuList.translation.y = layoutHeight
	}

	override fun onEvent(e: UIEvent) {
		mouse_handler!!.handle(e)
	}

	private fun toggle() {
		if (menuList.parent == null)
			add(menuList)
		else
			remove(menuList)
	}
}
