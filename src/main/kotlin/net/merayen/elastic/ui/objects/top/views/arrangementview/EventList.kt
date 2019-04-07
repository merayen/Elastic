package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class EventList : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox()), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val arrangementGrid = ArrangementGrid()
	private val arrangementEventTracks = UIObject()

	override fun onInit() {
		add(arrangementEventTracks)
		add(arrangementGrid)
	}

	override fun onUpdate() {
		super.onUpdate()
		arrangementGrid.layoutWidth = layoutWidth
		arrangementGrid.layoutHeight = layoutHeight

		for (obj in search.children) {
			if (obj is EventPane)
				obj.layoutWidth = layoutWidth
		}

		placement.maxWidth = layoutWidth
	}
}