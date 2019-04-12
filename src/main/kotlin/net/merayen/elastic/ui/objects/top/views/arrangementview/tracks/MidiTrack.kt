package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.buttons.StateButton
import net.merayen.elastic.ui.objects.components.TextInput
import net.merayen.elastic.ui.objects.top.views.arrangementview.Arrangement
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementTrack
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.EventTimeLine

class MidiTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	private var muteButton: StateButton
	private var soloButton: StateButton
	private var recordButton: StateButton

	private val trackName = TextInput()

	private val eventTimeLine = EventTimeLine()

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

		eventTimeLine.handler = object : EventTimeLine.Handler {
			override fun onEventMove(eventId: String, position: Float) {
				val query = HashMap<String, Any>()
				val moveEvent = HashMap<String, String>()

				moveEvent["eventId"] = eventId
				query["moveEvent"] = moveEvent

				arrangement.sendMessage(NodeDataMessage(nodeId, query))
			}
		}
	}

	override fun onParameter(key: String, value: Any) {
		when (key) {
			"mute" -> muteButton.value = value as Boolean
			"solo" -> soloButton.value = value as Boolean
			"record" -> recordButton.value = value as Boolean
			"trackName" -> trackName.value = value as String
		}
	}
}