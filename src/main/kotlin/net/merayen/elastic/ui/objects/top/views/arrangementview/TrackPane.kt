package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class TrackPane : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 50f

	val buttons = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox())

	init {
		add(buttons)
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}