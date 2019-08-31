package net.merayen.elastic.ui.objects.components.midiroll

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.util.Point

/**
 * Draws over and disables parts of the score that has no event zones.
 * Makes so that the user can not click on it.
 * TODO allow adding event zone in those dark zones?
 */
internal class EventDarkZones : UIObject(), FlexibleDimension {
	interface Handler {
		fun onCreateEventZone(start: Float)
	}

	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private var startStops = ArrayList<StartStop>()

	private inner class Zone : UIObject() {
		var zoneWidth = 0f

		private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
		private val createZoneMenuItem = TextContextMenuItem("Create zone")

		override fun onInit() {
			contextMenu.addMenuItem(createZoneMenuItem)
			contextMenu.handler = object : ContextMenu.Handler {
				override fun onSelect(item: ContextMenuItem?, position: Point) {
					if (item == createZoneMenuItem)
						handler?.onCreateEventZone((translation.x + position.x) / beatWidth)
				}

				override fun onMouseDown(position: Point) {}
			}
		}

		override fun onEvent(event: UIEvent) {
			contextMenu.handle(event)
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0f, 0f, 0f, 0.8f)
			draw.fillRect(0f, 0f, zoneWidth, layoutHeight)
		}
	}

	var handler: Handler? = null

	var beatWidth = 0f

	private val zones = ArrayList<Zone>()

	fun applyDarkZones(startStops: ArrayList<StartStop>) {
		this.startStops = startStops

		updateDarkZones()
	}

	fun updateDarkZones() {
		// Remove any none-showing dark zone
		//val startStops = startStops.filter { (it.start + it.length) * beatWidth <  && }

		for (i in 0 until startStops.size - zones.size) {
			val zone = Zone()
			zones.add(zone)
			add(zone)
			println("Added zone $zone")
		}

		for (i in 0 until zones.size - startStops.size) {
			val zone = zones[zones.size - 1]
			zones.removeAt(zones.size - 1)
			remove(zone)
			println("Remove zone $zone")
		}

		for (i in 0 until zones.size) {
			val startStop = startStops[i]


			zones[i].translation.x = if (startStop.start == Float.NEGATIVE_INFINITY) 0f else startStop.start * beatWidth
			zones[i].zoneWidth = if (startStop.length == Float.POSITIVE_INFINITY) layoutWidth else startStop.length * beatWidth
			println("$i,${zones[i].translation.x},${zones[i].zoneWidth}")
		}
	}
}