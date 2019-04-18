package net.merayen.elastic.ui.objects.dialogs

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.util.UINodeUtil

class TextInputDialog(private val description: String, private val value: String, private val onDone: (value: String?) -> Unit) : UIObject() {
	interface Handler {
		fun onDone(value: String?)
	}

	override fun onInit() {
		UINodeUtil.getWindow(this)?.nativeUI?.dialog?.showTextInput(description, value) { value -> onDone(value) }
	}
}