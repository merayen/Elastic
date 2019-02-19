package net.merayen.elastic.ui.objects.dialogs

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.NativeUIController

class TextInputDialog : UIObject() {
	override fun onInit() {
		sendMessage(NativeUIController.ShowInputTextDialogMessage("Track name"))
	}
}