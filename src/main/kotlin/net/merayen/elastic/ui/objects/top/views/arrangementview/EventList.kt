package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class EventList : UIObject(), FlexibleDimension {
	interface Handler {
		fun onPlayheadMoved(position: Float)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var beatWidth = 20f

	private val arrangementGrid = ArrangementGrid()
	private val arrangementEventTracks = UIObject()
	private val eventPane = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox())
	private val playhead = Playhead()

	override fun onInit() {
		add(arrangementEventTracks)
		add(arrangementGrid)
		add(eventPane)
		add(playhead) // Always on top

		arrangementEventTracks.translation.y = 20f
		arrangementGrid.translation.y = 20f
		eventPane.translation.y = 20f

		playhead.handler = object : Playhead.Handler {
			override fun onMoved(position: Float) {

			}
		}
	}

	override fun onUpdate() {
		arrangementGrid.layoutWidth = layoutWidth
		arrangementGrid.layoutHeight = layoutHeight

		for (obj in eventPane.search.children) {
			if (obj is EventPane) {
				obj.layoutWidth = layoutWidth
				obj.beatWidth = beatWidth
			}
		}

		eventPane.placement.maxWidth = layoutWidth

		playhead.layoutHeight = layoutHeight
	}

	fun addEventPane(eventPane: EventPane) = this.eventPane.add(eventPane)
	fun removeEventPane(eventPane: EventPane) = this.eventPane.remove(eventPane)
}