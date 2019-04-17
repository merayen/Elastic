package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.components.buttons.Button

abstract class BaseEditPane : UIClip() {
	private val backButton = Button("Back")
	val content = UIObject()

	override fun onInit() {
		add(backButton)
	}
}