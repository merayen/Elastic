package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.curvebox.BezierCurveBox
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.EmptyContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.util.Point

class EventTimeLineGraph : UIObject(), FlexibleDimension {
	interface Handler {
		fun onHide()
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	var handler: Handler? = null

	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	private val closeMenuItem = TextContextMenuItem("Hide graph")

	private val curve = BezierCurveBox()

	override fun onInit() {
		add(curve)

		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(EmptyContextMenuItem())
		contextMenu.addMenuItem(closeMenuItem)
		contextMenu.handler = object : ContextMenu.Handler {
			override fun onSelect(item: ContextMenuItem?, position: Point) {
				when (item) {
					closeMenuItem -> handler?.onHide()
				}
			}

			override fun onMouseDown(position: Point) {}
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0f, 0f, 0f, 0.5f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		curve.layoutWidth = layoutWidth
		curve.layoutHeight = layoutHeight
	}

	override fun onEvent(event: UIEvent) {
		contextMenu.handle(event)
	}
}