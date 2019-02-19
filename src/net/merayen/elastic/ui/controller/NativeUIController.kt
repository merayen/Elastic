package net.merayen.elastic.ui.controller

import net.merayen.elastic.util.Postmaster
import java.util.*
import javax.swing.JOptionPane

/**
 * Send messages to this controller and it will call native ui components, dependent on the platform.
 */
class NativeUIController internal constructor(gate: Gate) : Controller(gate) {
	private val afterDrawQueue = ArrayDeque<Runnable>()

	class ShowInputTextDialogMessage(val text: String) : Postmaster.Message()
	class ShowInputTextDialogResponseMessage(val text: String) : Postmaster.Message()

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {} // Not applicable

	override fun onMessageFromUI(message: Postmaster.Message) {
		if (message is ShowInputTextDialogMessage) {
			afterDrawQueue.add(Runnable {
				val text = JOptionPane.showInputDialog(message.text)
				if (text != null) {
					ShowInputTextDialogResponseMessage(text)
				}
			})
		}
	}

	override fun onAfterDraw() {
		synchronized(afterDrawQueue) {
			while (!afterDrawQueue.isEmpty())
				afterDrawQueue.pollFirst().run()
		}
	}
}