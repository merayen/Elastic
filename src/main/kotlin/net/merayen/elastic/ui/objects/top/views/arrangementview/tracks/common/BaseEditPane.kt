package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.buttons.Button

abstract class BaseEditPane : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	private val backButton = Button("Back")
	val content = UIObject()

	override fun onInit() {
		add(backButton)
	}
}