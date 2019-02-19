package net.merayen.elastic.ui.objects.top

import net.merayen.elastic.ui.UIObject
import javax.swing.JOptionPane

/**
 * Makes native dialogs (like text input) available for use in Elastic
 */
class NativeDialogs {
	class ShowInputText(description: String, sender: UIObject) {


		init {
			JOptionPane.showInputDialog("Input your text")
		}
	}
}