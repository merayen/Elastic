package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.system.intercom.RemoveNodeMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.util.MutablePoint

internal class TitleBarContextMenu(titlebar: UIObject) : UIObject() {
	private val menu: ContextMenu

	private val editNode = TextContextMenuItem("Edit")
	private val deleteNode = TextContextMenuItem("Delete")

	init {
		menu = ContextMenu(titlebar, MouseEvent.Button.RIGHT)

		menu.handler = object : ContextMenu.Handler {
			override fun onMouseDown(position: MutablePoint) {}

			override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
				if (item === deleteNode) {
					val nodeId = search.parentByType(UINode::class.java)?.nodeId
					if(nodeId != null)
						sendMessage(RemoveNodeMessage(nodeId))
				} else if(item === editNode) {
					/*val node = search.parentByType(UINode::class.java)
					if(node != null && node is INodeEditable)
						sendMessage(EditNodeMessage(node))*/ // TODO fix dette? Finne random view? Bruke eksisterende view? Popup?
				}
			}
		}

		menu.addMenuItem(EmptyContextMenuItem())
		menu.addMenuItem(EmptyContextMenuItem())
		menu.addMenuItem(editNode)
		menu.addMenuItem(EmptyContextMenuItem())
		menu.addMenuItem(deleteNode)
	}

	override fun onEvent(event: UIEvent) {
		menu.handle(event)
	}
}