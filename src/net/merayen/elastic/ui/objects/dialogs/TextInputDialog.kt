package net.merayen.elastic.ui.objects.dialogs

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.Top

class TextInputDialog(private val description: String, private val value: String, private val onDone: (value: String?) -> Unit) : UIObject() {
	interface Handler {
		fun onDone(value: String?)
	}

	override fun onInit() {
		(search.top as Top).nativeUI.ShowInputTextDialog(description, value) { value -> onDone(value) }
	}
}