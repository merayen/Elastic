package net.merayen.elastic.backend.context;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.ResetNetListMessage;
import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.NetListMessages;

/**
 * Helper class for BackendContext.
 * Routes messages between UI, LogicNodes and Processor
 */
public class MessageHandler {
	private final BackendContext backend_context;
	private final Postmaster to_ui = new Postmaster();
	private final Postmaster to_backend = new Postmaster();

	MessageHandler(BackendContext bc) {
		this.backend_context = bc;
	}

	/**
	 * Messages sent from LogicNode further into backend is handled here.
	 */
	void handleFromLogicToProcessor(Postmaster.Message message) {
		backend_context.dispatch.executeMessage(message);
	}

	void handleFromLogicToUI(Postmaster.Message message) {
		to_ui.send(message);
	}

	/**
	 * Handles messages sent from UI.
	 */
	public void handleFromUI(Postmaster.Message message) {
		to_backend.send(message);
	}

	void executeMessagesToBackend() {
		Postmaster.Message message;
		while((message = to_backend.receive()) != null) {
			if(message instanceof NetListRefreshRequestMessage) {
				NetListRefreshRequestMessage m = (NetListRefreshRequestMessage)message;

				List<Postmaster.Message> refresh_messages = new ArrayList<>();
				refresh_messages.add(new ResetNetListMessage(m.group_id)); // This will clear the receiver's NetList
				refresh_messages.addAll(NetListMessages.disassemble(backend_context.logicnode_supervisor.getNetList(), m.group_id)); // All these messages will rebuild the receiver's NetList

				to_ui.send(refresh_messages); // Send all messages in a chunk so no other messages can get in-between.
				break;
			}

			backend_context.logicnode_supervisor.handleMessageFromUI(message);
		}
	}

	public Postmaster.Message[] receiveMessagesFromBackend() {
		return to_ui.receiveAll();
	}

	void handleFromProcessor(Postmaster.Message message) {
		if(message instanceof ProcessMessage)
			backend_context.logicnode_supervisor.handleResponseFromProcessing((ProcessMessage)message);
		else {} // XXX handle misc messages from processor? (crash message etc)
	}
}
