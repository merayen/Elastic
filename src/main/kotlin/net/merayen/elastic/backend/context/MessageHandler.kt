package net.merayen.elastic.backend.context

import net.merayen.elastic.backend.context.action.ImportFileIntoNodeGroupAction
import java.util.ArrayList

import net.merayen.elastic.backend.context.action.LoadProjectAction
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage
import net.merayen.elastic.system.intercom.ProcessMessage
import net.merayen.elastic.system.intercom.FinishResetNetListMessage
import net.merayen.elastic.system.intercom.BeginResetNetListMessage
import net.merayen.elastic.system.intercom.backend.*
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.NetListMessages

/**
 * Helper class for BackendContext.
 * Routes messages between UI, LogicNodes and Processor
 */
class MessageHandler internal constructor(private val backendContext: BackendContext) {
    private val to_ui = Postmaster()
    private val to_backend = Postmaster()
    private val from_processor = Postmaster()

    /**
     * Messages sent from LogicNode further into backend is handled here.
     */
    fun handleFromLogicToProcessor(message: Any) {
        backendContext.dispatch.executeMessage(message)
    }

    fun handleFromLogicToUI(message: Any) {
        to_ui.send(message)
    }

    /**
     * Handles messages to backend.
     */
    fun sendToBackend(message: Any) {
        to_backend.send(message)
    }

    fun sendToBackend(messages: List<Any>) {
        to_backend.send(messages)
    }

    fun executeMessagesToBackend() {
        var message: Any?
        while (true) {
            message = to_backend.receive() ?: return

            when (message) {
                is NetListRefreshRequestMessage -> {
                    val m = message as NetListRefreshRequestMessage?

                    val refresh_messages = ArrayList<Any>()
                    refresh_messages.add(BeginResetNetListMessage(m!!.group_id)) // This will clear the receiver's NetList
                    refresh_messages.addAll(NetListMessages.disassemble(backendContext.env.project.netList, m.group_id!!)) // All these messages will rebuild the receiver's NetList
                    refresh_messages.add(FinishResetNetListMessage())

                    to_ui.send(refresh_messages) // Send all messages in a chunk so no other messages can get in-between.

                }
                is CreateCheckpointMessage -> backendContext.env.project.checkpoint.create()
                is TidyProjectMessage -> backendContext.env.project.tidy()
                is InitBackendMessage -> LoadProjectAction().start(backendContext)
                is StartBackendMessage -> backendContext.start()
                is ImportFileIntoNodeGroupMessage -> ImportFileIntoNodeGroupAction(message).start(backendContext)
                else -> backendContext.logicnode_supervisor.handleMessageFromUI(message)
            }
        }
    }

    fun receiveMessagesFromBackend(): Array<Any> {
        return to_ui.receiveAll()
    }

    fun queueFromProcessor(message: Any) {
        from_processor.send(message)
    }

    fun executeMessagesFromProcessor() {
        var message: Any?
        while (true) {
            message = from_processor.receive() ?: return

            when (message) {
                is ProcessMessage -> backendContext.logicnode_supervisor.handleResponseFromProcessor(message as ProcessMessage?)
                else -> {} // XXX handle misc messages from processor? (crash message etc)
            }
        }
    }
}
