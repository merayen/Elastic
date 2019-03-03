package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.util.Postmaster

class ArrangementEventView : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	internal val arrangementGrid = ArrangementGrid()
	internal val arrangementEventTracks = UIObject()

	override fun onInit() {
		add(arrangementGrid)
		add(arrangementEventTracks)
	}

	override fun onUpdate() {
		arrangementGrid.layoutWidth = layoutWidth
		arrangementGrid.layoutHeight = layoutHeight
	}

	fun handleMessage(message: Postmaster.Message) {
		when(message) {
			is CreateNodeMessage -> {
				if (message.name == "midi") {
					arrangementEventTracks.add(ArrangementEventTrack(ArrangementData.TrackData.Type.MIDI))
				}
			}
		}
	}
}