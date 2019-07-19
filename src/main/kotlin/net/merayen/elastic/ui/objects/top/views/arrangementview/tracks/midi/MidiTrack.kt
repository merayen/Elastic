package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi

import net.merayen.elastic.backend.logicnodes.list.midi_1.*
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.UIObject
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
				handler = object : Handler {
					override fun onClick(value: Boolean) {
						sendParameter(Data(mute = value))
					}
				}
			}
		}

		soloButton = object : StateButton() {
			init {
				label = "S"
				textColor = Color()
				backgroundColor = Color(1f, 1f, 0f)
				handler = object : Handler {
					override fun onClick(value: Boolean) {
						sendParameter(Data(solo = value))
					}
				}
			}
		}

		recordButton = object : StateButton() {
			init {
				label = "R"
				textColor = Color(1f, 1f, 1f)
				backgroundColor = Color(1f, 0.5f, 0.5f)
				handler = object : Handler {
					override fun onClick(value: Boolean) {
						sendParameter(Data(record = value))
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
				sendParameter(Data(trackName = text))
			}
		}
		trackPane.add(trackName)

		eventPane.timeLine = eventTimeLine
		eventPane.editPane = midiEditPane

		eventTimeLine.handler = object : EventTimeLine.Handler {
			override fun onEventSelect() = handler?.onEventSelect() ?: Unit

			override fun onCreateEvent(id: String, start: Float, length: Float) {
				arrangement.sendMessage(AddEventZoneMessage(nodeId, id, start, length))
			}

			override fun onRemoveEvent(id: String) {
				arrangement.sendMessage(RemoveEventZoneMessage(nodeId, id))
			}

			override fun onRepeatEvent(id: String, count: Int) {
				val event = eventTimeLine.getEvent(id)
				if (event != null) {
					for (i in 0 until count) {
						arrangement.sendMessage(
								AddEventZoneMessage(
										nodeId,
										UniqueID.create(),
										event.start + event.length + event.length * i,
										event.length
								)
						)
					}
				}
			}

			override fun onChangeEvent(eventId: String, position: Float, length: Float) {
				arrangement.sendMessage(ChangeEventZoneMessage(nodeId, eventId, position, length))
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

	override fun onParameter(instance: BaseNodeData) {
		val data = instance as Data
		val mute = data.mute
		val solo = data.solo
		val record = data.record
		val trackNameData = data.trackName
		val eventZones = data.eventZones

		if (mute != null) muteButton.value = mute
		if (solo != null) soloButton.value = solo
		if (record != null) recordButton.value = record
		if (trackNameData != null) trackName.value = trackNameData
		if (eventZones != null) {
			eventTimeLine.loadEventZones(eventZones)

			// Exit edit mode if we get updates on events and the event has disappeared
			val editEventZone = midiEditPane.eventZone
			if (editEventZone != null && eventTimeLine.getEvent(editEventZone.id) == null) {
				eventPane.editMode = false
				midiEditPane.eventZone = null
			}
		}
	}

	override fun onSelectionRectangle(selectionRectangle: UIObject) {
		eventTimeLine.onSelectionRectangle(selectionRectangle)
	}

	override fun clearSelections() {
		eventTimeLine.clearSelections()
	}
}
