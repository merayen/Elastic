package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.backend.logicnodes.list.group_1.PlaybackStatusMessage
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.ArrangementController
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.SelectionRectangle
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi.MidiTrack
import net.merayen.elastic.util.MutablePoint

class Arrangement : UIObject() {
	var layoutWidth: Float = 0f
	var layoutHeight: Float = 0f

	var beatWidth = 40f

	private val tracks = ArrayList<ArrangementTrack>()
	private val trackList = TrackList()
	private val eventList = EventList()

	private val arrangementListScroll = Scroll(eventList)
	private val buttonBar = AutoLayout(LayoutMethods.HorizontalBox(5f, 100000f))

	private val selectionRectangle = SelectionRectangle()

	override fun onInit() {
		add(arrangementListScroll)
		add(buttonBar)
		add(trackList)

		trackList.translation.y = 20f

		arrangementListScroll.translation.x = 100f
		arrangementListScroll.translation.y = 20f

		buttonBar.add(object : Button() {
			init {
				label = "New track"
				handler = object : IHandler {
					override fun onClick() = TODO("Ask user which type of track to create and send a message to backend")
				}
				disabled = true // For now
			}
		})

		eventList.handler = object : EventList.Handler {
			override fun onPlayheadMoved(beat: Float) = sendMessage(ArrangementController.PlayheadPositionChange(beat))
			override fun onRangeChange(range: Pair<Float, Float>) = sendMessage(ArrangementController.RangeSelectionChange(range))
		}
	}

	override fun onUpdate() {
		trackList.layoutWidth = 100f
		arrangementListScroll.layoutWidth = layoutWidth
		arrangementListScroll.layoutHeight = layoutHeight - 20

		for (track in tracks) // Make EventPane keep up with the height of the TrackPane
			track.eventPane.layoutHeight = track.trackPane.layoutHeight

		eventList.beatWidth = beatWidth
	}

	fun handleMessage(message: ElasticMessage) {
		when (message) {
			is CreateNodeMessage -> {
				if (message.name == "midi") {
					val midiTrack = MidiTrack(message.nodeId, this)
					midiTrack.handler = object : MidiTrack.Handler {
						override fun onEventSelect() {
							for (track in tracks) // TODO check for modifier-key. If user is holding SHIFT, do not unselect everything
								track.clearSelections()
						}

						var startPosition: MutablePoint? = null

						override fun onSelectionDrag(start: MutablePoint, offset: MutablePoint) {
							val pos = getRelativePosition(midiTrack.eventPane) ?: return

							val startPosition = startPosition ?: start
							this.startPosition = startPosition

							if (selectionRectangle.parent == null)
								add(selectionRectangle)

							val x = if (offset.x >= 0) pos.x + startPosition.x else startPosition.x + pos.x + offset.x
							val y = if (offset.y >= 0) pos.y + startPosition.y else startPosition.y + pos.y + offset.y
							val width = if (offset.x >= 0) offset.x else -offset.x
							val height = if (offset.y >= 0) offset.y else -offset.y

							selectionRectangle.translation.x = x
							selectionRectangle.translation.y = y

							selectionRectangle.layoutWidth = width
							selectionRectangle.layoutHeight = height

							for (track in tracks)
								track.onSelectionRectangle(selectionRectangle)
						}

						override fun onSelectionDrop(start: MutablePoint, offset: MutablePoint) {
							if (selectionRectangle.parent != null)
								remove(selectionRectangle)

							startPosition = null
						}
					}
					trackList.add(midiTrack.trackPane)
					eventList.addEventPane(midiTrack.eventPane)
					tracks.add(midiTrack)
				}
				eventList.handleMessage(message)
			}
			is PlaybackStatusMessage -> eventList.handleMessage(message)
			is NodePropertyMessage -> eventList.handleMessage(message)
		}

		for (track in tracks) {
			if (message is NodePropertyMessage) {
				if (message.nodeId == track.nodeId)
					track.onParameter(message.instance)
			}
		}
	}

	private fun removeTrack(track: ArrangementTrack) {
		trackList.remove(track.trackPane)
		eventList.removeEventPane(track.eventPane)
		tracks.remove(track)
	}
}
