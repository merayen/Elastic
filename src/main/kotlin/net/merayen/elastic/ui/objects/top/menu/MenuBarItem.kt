package net.merayen.elastic.ui.objects.top.menu

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.MutablePoint

class MenuBarItem(label: String) : UIObject() {

	var label = ""
	var menu_list = MenuList()

	private var handler: Handler? = null
	var labelWidth: Float = 0.toFloat()
		private set
	private var mouse_handler: MouseHandler? = null
	private var over = false
	private var allow_closing: Long = 0
	private var close_menu: Boolean = false

	/*
	 * You know what this is, so no need to write anything in the description here. TODO Remove description
	 */
	abstract class Handler {
		open fun onOpen() {}
	}

	init {
		this.label = label
	}

	override fun onInit() {
		menu_list.translation.y = 20f
		menu_list.setHandler(object : MenuList.Handler() {
			override fun onOutsideClick() {
				if (allow_closing < System.currentTimeMillis())
					close_menu = true
			}
		})
		mouse_handler = MouseHandler(this)
		mouse_handler!!.setHandler(object : MouseHandler.Handler() {
			override fun onMouseOver() {
				over = true
			}

			override fun onMouseOut() {
				over = false
			}

			override fun onMouseDown(position: MutablePoint) {
				toggleMenu()
			}
		})
	}

	override fun onDraw(draw: Draw) {
		draw.setFont("Geneva", 12f)
		labelWidth = draw.getTextWidth(label)

		draw.setColor(80, 80, 80)
		draw.fillRect(0f, 0f, labelWidth + 10f, 18f)

		draw.setColor(80, 80, 80)
		draw.text(label, 5f, 15f)

		if (over)
			draw.setColor(255, 255, 200)
		else
			draw.setColor(200, 200, 200)

		draw.text(label, 4.5f, 14.5f)
	}

	override fun onUpdate() {
		if (close_menu) {
			hideMenu()
			close_menu = false
		}
	}

	override fun onEvent(event: UIEvent) {
		mouse_handler!!.handle(event)
	}

	private fun toggleMenu() {
		if (handler != null) handler!!.onOpen()
		showMenu()
	}

	fun showMenu() {
		if (menu_list.parent == null)
			add(menu_list)

		allow_closing = System.currentTimeMillis() + 200
	}

	fun hideMenu() {
		if (menu_list.parent != null)
			remove(menu_list)
	}

	fun setHandler(handler: Handler) {
		this.handler = handler
	}
}
