package net.merayen.elastic.ui.objects.components.midiroll.eventzone

import net.merayen.elastic.backend.data.eventdata.MidiData
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.contextmenu.ContextMenu
import net.merayen.elastic.ui.objects.contextmenu.ContextMenuItem
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.util.MutablePoint
import kotlin.math.max

class MidiRollEventZones(val octaveCount: Int) : UIObject() {
	interface Handler {
		fun onCreateEventZone(start: Float, length: Float)

		/**
		 * Called if user changes the start or stop position of the event zone
		 */
		fun onChangeEventZone(eventZoneId: String, start: Float, length: Float)

		fun onAddMidi(eventZoneId: String, midiData: MidiData)
		fun onRemoveMidi(eventZoneId: String, id: String)

		fun onGhostNote(tangent: Short)
		fun onGhostNoteOff(tangent: Short)
	}

	var layoutHeight = 0f

	var handler: Handler? = null

	var beatWidth = 10f

	private val eventZones = ArrayList<MidiRollEventZone>()

	private val contextMenu = ContextMenu(this, MouseEvent.Button.RIGHT)
	private val createEventZone = TextContextMenuItem("Create zone")

	override fun onInit() {
		contextMenu.addMenuItem(createEventZone)

		contextMenu.handler = object : ContextMenu.Handler {
			override fun onSelect(item: ContextMenuItem?, position: MutablePoint) {
				handler?.onCreateEventZone(0f, 4f)
			}

			override fun onMouseDown(position: MutablePoint) {}
		}
	}

	fun handleMessage(message: NodePropertyMessage) {
		val data = message.instance as Properties

		val newEventZones = data.eventZones

		if (newEventZones != null) {
			eventZones.forEach { remove(it) }
			eventZones.clear()

			for (newZone in newEventZones) {
				val newZoneId = newZone.id!!
				val m = MidiRollEventZone(newZoneId, this)
				m.start = newZone.start!!
				m.length = newZone.length!!
				m.handler = object: MidiRollEventZone.Handler {
					override fun onResize(offsetPosition: Float, offsetLength: Float) {
						handler?.onChangeEventZone(newZoneId, max(0f, m.start + offsetPosition), max(0f, m.length + offsetLength))
					}
				}

				eventZones.add(m)
				add(m)
			}

			eventZones.sortBy { it.start }
		}
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0f, 0f, 1f)
		draw.fillRect(0f, 0f, 100f, layoutHeight)
	}

	override fun onUpdate() {
		for (zone in eventZones) {
			zone.layoutHeight = layoutHeight
			zone.beatWidth = beatWidth
		}
	}

	override fun onEvent(event: UIEvent) {
		contextMenu.handle(event)
	}
}