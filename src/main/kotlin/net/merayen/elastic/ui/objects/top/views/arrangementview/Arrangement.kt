package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.SelectionRectangle
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi.MidiTrack
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.Postmaster

class Arrangement : UIObject() {
	var layoutWidth: Float = 0f
	var layoutHeight: Float = 0f

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
				handler = object : Button.IHandler {
					override fun onClick() {
						TODO("Ask user which type of track to create and send a message to backend")
					}
				}
			}
		})
	}

	override fun onUpdate() {
		trackList.layoutWidth = 100f
		arrangementListScroll.layoutWidth = layoutWidth
		arrangementListScroll.layoutHeight = layoutHeight - 20

		eventList.layoutWidth = layoutWidth - 100
		eventList.layoutHeight = layoutHeight - 20

		for (track in tracks) // Make EventPane keep up with the height of the TrackPane
			track.eventPane.layoutHeight = track.trackPane.layoutHeight
	}

	fun handleMessage(message: Postmaster.Message) {
		when (message) {
			is BeginResetNetListMessage -> {
				ArrayList(tracks).forEach { removeTrack(it) }
			}
			is CreateNodeMessage -> {
				if (message.name == "midi") {
					val midiTrack = MidiTrack(message.nodeId, this)
					midiTrack.handler = object : MidiTrack.Handler {
						override fun onEventSelect() {
							for (track in tracks) // TODO check for modifer-key. If user is holding SHIFT, do not unselect everything
								track.clearSelections()
						}

						var startPosition: Point? = null

						override fun onSelectionDrag(start: Point, offset: Point) {
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

						override fun onSelectionDrop(start: Point, offset: Point) {
							if (selectionRectangle.parent != null)
								remove(selectionRectangle)

							startPosition = null
						}
					}
					trackList.add(midiTrack.trackPane)
					eventList.add(midiTrack.eventPane)
					tracks.add(midiTrack)
				}
			}
		}

		for (track in tracks) {
			if (message is NodeParameterMessage)
				if (message.nodeId == track.nodeId)
					track.onParameter(message.key, message.value)
		}
	}

	private fun removeTrack(track: ArrangementTrack) {
		trackList.remove(track.trackPane)
		eventList.remove(track.eventPane)
		tracks.remove(track)
	}
}