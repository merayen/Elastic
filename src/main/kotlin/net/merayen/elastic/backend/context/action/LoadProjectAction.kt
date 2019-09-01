package net.merayen.elastic.backend.context.action

import net.merayen.elastic.backend.context.Action
import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.FinishResetNetListMessage
import net.merayen.elastic.util.NetListMessages
import java.io.File
import java.util.*

/**
 * Loads a project. Automatically done when initiating the backend.
 */
class LoadProjectAction : Action() {
	override fun run() {
		if (!File(env.project.path).isDirectory)
			throw RuntimeException("Project not found. Should not happen as it should have been created or already exist")

		val messages = ArrayList<ElasticMessage>()

		messages.add(BeginResetNetListMessage())
		messages.addAll(NetListMessages.disassemble(env.project.data.rawNetList))
		messages.add(FinishResetNetListMessage())

		backendContext.message_handler.sendToBackend(messages)
	}
}
