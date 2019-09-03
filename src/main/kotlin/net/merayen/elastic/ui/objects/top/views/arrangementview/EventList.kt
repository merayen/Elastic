package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class EventList : /*AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox())*/ UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	var beatWidth = 20f

	private val arrangementGrid = ArrangementGrid()
	private val arrangementEventTracks = UIObject()
	private val eventPane = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox())

	override fun onInit() {
		add(arrangementEventTracks)
		add(arrangementGrid)
		add(eventPane)
	}

	override fun onUpdate() {
		if (arrangementGrid.translation.x > 500)
			println("NEIE")

		super.onUpdate()

		arrangementGrid.layoutWidth = layoutWidth
		arrangementGrid.layoutHeight = layoutHeight

		//println("${System.identityHashCode(arrangementGrid)}\t${arrangementGrid.translation.x}")

		for (obj in eventPane.search.children) {
			if (obj is EventPane) {
				obj.layoutWidth = layoutWidth
				obj.beatWidth = beatWidth
			}
		}

		eventPane.placement.maxWidth = layoutWidth
	}

	fun addEventPane(eventPane: EventPane) {
		this.eventPane.add(eventPane)
	}

	fun removeEventPane(eventPane: EventPane) {
		this.eventPane.remove(eventPane)
	}
}