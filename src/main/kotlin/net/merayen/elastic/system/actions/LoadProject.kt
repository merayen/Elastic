package net.merayen.elastic.system.actions

import net.merayen.elastic.system.Action
import net.merayen.elastic.system.intercom.backend.InitBackendMessage
import net.merayen.elastic.system.intercom.ui.InitUIMessage

/**
 * Loads a main project.
 */
class LoadProject(private val path: String) : Action() {
	override fun run() {
		system.end()
		system.sendMessageToUI(listOf(InitUIMessage()))
		system.sendMessageToBackend(listOf(InitBackendMessage(44100, 16, 1024, path)))
	}
}
