package net.merayen.elastic.ui.util

import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class NativeUI {
	inner class ShowInputTextDialog(description: String = "", value: String = "", onDone: (value: String?) -> Unit) {
		init {
			SwingUtilities.invokeLater {
				onDone(JOptionPane.showInputDialog(description, value))
			}
		}
	}
}