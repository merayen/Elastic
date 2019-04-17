package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi

import net.merayen.elastic.backend.logicnodes.list.midi_1.AddEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.ChangeEventZoneMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Parameters
import net.merayen.elastic.backend.logicnodes.list.midi_1.RemoveEventZoneMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.buttons.StateButton
import net.merayen.elastic.ui.objects.components.TextInput
import net.merayen.elastic.ui.objects.top.views.arrangementview.Arrangement
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementTrack
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.EventTimeLine
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.UniqueID

class MidiTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	interface Handler : ArrangementTrack.Handler

	private val muteButton: StateButton
	private val soloButton: StateButton
	private val recordButton: StateButton

	private val trackName = TextInput()

	private val eventTimeLine = EventTimeLine()
	private val midiEditPane = MidiEditPane(nodeId)

	var handler: Handler? = null

	init {
		val removeButton = Button()
		removeButton.label = "X"
		removeButton.handler = object : Button.IHandler {
			override fun onClick() {
				// TODO delete just the node, or its followers too? Like if there is a poly-node following right after?
			}
		}

		muteButton = object : StateButton() {
			init {
				label = "M"
				textColor = Color(1f, 1f, 1f)
				backgroundColor = Color(1f, 0f, 0f)
				handler = object : StateButton.Handler {
					override fun onClick(value: Boolean) {
						sendParameter("mute", value)
					}
				}
			}
		}

		soloButton = object : StateButton() {
			init {
				label = "S"
				textColor = Color()
				backgroundColor = Color(1f, 1f, 0f)
				handler = object : StateButton.Handler {
					override fun onClick(value: Boolean) {
						sendParameter("solo", value)
					}
				}
			}
		}

		recordButton = object : StateButton() {
			init {
				label = "R"
				textColor = Color(1f, 1f, 1f)
				backgroundColor = Color(1f, 0.5f, 0.5f)
				handler = object : StateButton.Handler {
					override fun onClick(value: Boolean) {
						sendParameter("record", value)
					}
				}
			}
		}

		trackPane.buttons.add(muteButton)
		trackPane.buttons.add(soloButton)
		trackPane.buttons.add(recordButton)
		trackName.translation.x = 5f
		trackName.translation.y = 25f
		trackName.description = "Name of the track"
		trackName.handler = object : TextInput.Handler {
			override fun onChange(text: String) {
				sendParameter("trackName", text)
			}
		}
		trackPane.add(trackName)

		eventPane.timeLine = eventTimeLine
		eventPane.editPane = midiEditPane

		eventTimeLine.handler = object : EventTimeLine.Handler {
			override fun onCreateEvent(id: String, start: Float, length: Float) {
				arrangement.sendMessage(NodeDataMessage(nodeId, AddEventZoneMessage(id, start, length)))
			}

			override fun onRemoveEvent(id: String) {
				arrangement.sendMessage(NodeDataMessage(nodeId, RemoveEventZoneMessage(id)))
			}

			override fun onRepeatEvent(id: String, count: Int) {
				val event = eventTimeLine.getEvent(id)
				if (event != null) {
					for (i in 0 until count) {
						arrangement.sendMessage(
								NodeDataMessage(
										nodeId,
										AddEventZoneMessage(
												UniqueID.create(),
												event.start + event.length + event.length * i,
												event.length
										)
								)
						)
					}
				}
			}

			override fun onChangeEvent(eventId: String, position: Float, length: Float) {
				arrangement.sendMessage(NodeDataMessage(nodeId, ChangeEventZoneMessage(eventId, position, length)))
			}

			override fun onEditEvent(id: String) {
				midiEditPane.eventZone = eventTimeLine.getEvent(id)
				eventPane.editMode = true
				trackPane.layoutHeight = 200f
			}

			override fun onSelectionDrag(start: Point, offset: Point) {
				handler?.onSelectionDrag(start, offset)
			}

			override fun onSelectionDrop(start: Point, offset: Point) {
				handler?.onSelectionDrop(start, offset)
			}
		}
	}

	override fun onParameter(key: String, value: Any) {
		when (key) {
			"mute" -> muteButton.value = value as Boolean
			"solo" -> soloButton.value = value as Boolean
			"record" -> recordButton.value = value as Boolean
			"trackName" -> trackName.value = value as String
			"eventZones" -> {
				eventTimeLine.loadEventZones((value as List<HashMap<String,Any>>).map { Parameters.EventZone(it) })

				// Exit edit mode if we get updates on events and the event has disappeared
				val editEventZone = midiEditPane.eventZone
				if (editEventZone != null && eventTimeLine.getEvent(editEventZone.id) == null) {
					eventPane.editMode = false
					midiEditPane.eventZone = null
				}
			}
		}
	}
}