package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class EventList : UIObject(), FlexibleDimension {
	interface Handler {
		fun onPlayheadMoved(beat: Float)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var beatWidth = 20f

	var handler: Handler? = null

	private val arrangementGrid = ArrangementGrid()
	private val arrangementEventTracks = UIObject()
	private val eventPanes = AutoLayout(LayoutMethods.HorizontalBox())
	private val playhead = Playhead()

	override fun onInit() {
		add(arrangementEventTracks)
		add(arrangementGrid)
		add(eventPanes)
		add(playhead) // Always on top

		arrangementEventTracks.translation.y = 20f
		arrangementGrid.translation.y = 20f
		eventPanes.translation.y = 20f

		playhead.handler = object : Playhead.Handler {
			override fun onMoved(beat: Float) {
				handler?.onPlayheadMoved(beat)
			}
		}
	}

	override fun onUpdate() {
		arrangementGrid.layoutWidth = layoutWidth
		arrangementGrid.layoutHeight = layoutHeight

		for (obj in eventPanes.search.children) {
			if (obj is EventPane) {
				obj.layoutWidth = layoutWidth
				obj.beatWidth = beatWidth
			}
		}

		eventPanes.placement.maxWidth = layoutWidth

		playhead.layoutHeight = layoutHeight
		playhead.beatWidth = beatWidth
	}

	fun addEventPane(eventPane: EventPane) = this.eventPanes.add(eventPane)
	fun removeEventPane(eventPane: EventPane) = this.eventPanes.remove(eventPane)

	fun setPlayheadPosition(beat: Float) {
		playhead.setPosition(beat)
	}
}