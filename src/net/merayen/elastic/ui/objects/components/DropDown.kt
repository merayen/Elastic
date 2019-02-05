package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent.Button
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.menu.MenuList
import net.merayen.elastic.ui.objects.top.menu.MenuListItem
import net.merayen.elastic.ui.util.MouseHandler
import net.merayen.elastic.util.Point

class DropDown : UIObject(), FlexibleDimension {
	interface Handler {
		fun onClick()
	}

	class Item(val dropdownItem: UIObject, val contextMenuItem: ContextMenuItem)

	override var layoutWidth = 100f
	override var layoutHeight = 20f

	private val items = ArrayList<Item>()
	private var currentItem: Item? = null

	private val contextMenu = ContextMenu(this, MouseEvent.Button.LEFT, object : ContextMenu.Handler {
		override fun onSelect(item: ContextMenuItem?, position: Point) {
			var selected: Item? = null

			for (m in items)
				if (m.contextMenuItem === item)
					selected = m

			if (selected != null)
				setViewItem(selected)
		}
	})

	override fun onDraw(draw: Draw) {
		draw.setColor(0, 0, 0)
		draw.setStroke(1f)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	fun addMenuItem(item: Item) {
		items.add(item)
		contextMenu.addMenuItem(item.contextMenuItem)
	}

	override fun onEvent(e: UIEvent) {
		contextMenu.handle(e)
	}

	private fun setViewItem(item: Item) {
		val cItem = currentItem
		if(cItem != null)
			remove(cItem.dropdownItem)

		if (item != null) {
			item.dropdownItem.translation.x = 10f
			item.dropdownItem.translation.y = 5f
			add(item.dropdownItem)
		}

		currentItem = item
	}
}
