package net.merayen.elastic.backend.context

import net.merayen.elastic.backend.context.action.ImportFileIntoNodeGroupAction
import net.merayen.elastic.backend.context.action.LoadProjectAction
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.system.intercom.backend.*
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import java.util.*

/**
 * Helper class for BackendContext.
 * Routes messages between UI, LogicNodes and Processor
 */
class MessageHandler internal constructor(private val backendContext: BackendContext) {
    private val to_ui = Postmaster<ElasticMessage>() // FIXME seem to get really big after a good while?
    private val to_backend = Postmaster<ElasticMessage>()
    private val from_processor = Postmaster<ElasticMessage>()

    /**
     * Messages sent from LogicNode further into backend is handled here.
     */
    fun handleFromLogicToProcessor(message: ElasticMessage) {
        backendContext.dispatch.executeMessage(message)
    }

    fun handleFromLogicToUI(message: ElasticMessage) {
        to_ui.send(message)
    }

    fun sendToBackend(messages: Collection<ElasticMessage>) {
        to_backend.send(messages = messages)
    }

    fun executeMessagesToBackend() {
        var message: ElasticMessage?
        while (true) {
            message = to_backend.receive() ?: return

            when (message) {
                is NetListRefreshRequestMessage -> {
                    val m = message as NetListRefreshRequestMessage?

                    val refresh_messages = ArrayList<ElasticMessage>()
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

    fun receiveMessagesFromBackend(): Collection<ElasticMessage> {
        return to_ui.receiveAll()
    }

    fun queueFromProcessor(message: ElasticMessage) {
        from_processor.send(message)
    }

    fun executeMessagesFromProcessor() {
        var message: ElasticMessage?
        while (true) {
            message = from_processor.receive() ?: return

            when (message) {
                is ProcessMessage -> backendContext.logicnode_supervisor.handleResponseFromProcessor(message as ProcessMessage?)
                else -> {} // XXX handle misc messages from processor? (crash message etc)
            }
        }
    }
}
