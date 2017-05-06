package net.merayen.elastic.backend.context;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.context.action.LoadProjectAction;
import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.FinishResetNetListMessage;
import net.merayen.elastic.system.intercom.BeginResetNetListMessage;
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.system.intercom.backend.StartBackendMessage;
import net.merayen.elastic.system.intercom.backend.TidyProjectMessage;
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
	private final Postmaster from_processor = new Postmaster();

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
	 * Handles messages to backend.
	 */
	public void sendToBackend(Postmaster.Message message) {
		to_backend.send(message);
	}

	public void sendToBackend(List<Postmaster.Message> messages) {
		to_backend.send(messages);
	}

	void executeMessagesToBackend() {
		Postmaster.Message message;
		while((message = to_backend.receive()) != null) {
			if(message instanceof NetListRefreshRequestMessage) {
				NetListRefreshRequestMessage m = (NetListRefreshRequestMessage)message;

				List<Postmaster.Message> refresh_messages = new ArrayList<>();
				refresh_messages.add(new BeginResetNetListMessage(m.group_id)); // This will clear the receiver's NetList
				refresh_messages.addAll(NetListMessages.disassemble(backend_context.env.project.getNetList(), m.group_id)); // All these messages will rebuild the receiver's NetList
				refresh_messages.add(new FinishResetNetListMessage());

				to_ui.send(refresh_messages); // Send all messages in a chunk so no other messages can get in-between.

			} else if(message instanceof CreateCheckpointMessage) {
				backend_context.env.project.checkpoint.create();

			} else if(message instanceof TidyProjectMessage) {
				backend_context.env.project.tidy();

			} else if(message instanceof InitBackendMessage) {
				new LoadProjectAction().start(backend_context);

			} else if(message instanceof StartBackendMessage) {
				backend_context.start();

			} else {
				backend_context.logicnode_supervisor.handleMessageFromUI(message);
			}
		}
	}

	public Postmaster.Message[] receiveMessagesFromBackend() {
		return to_ui.receiveAll();
	}

	void queueFromProcessor(Postmaster.Message message) {
		from_processor.send(message);
	}

	void executeMessagesFromProcessor() {
		Postmaster.Message message;
		while((message = from_processor.receive()) != null) {
			if(message instanceof ProcessMessage) {
				backend_context.logicnode_supervisor.handleResponseFromProcessor((ProcessMessage)message);
			} else {
				// XXX handle misc messages from processor? (crash message etc)
			}
		}
	}
}
