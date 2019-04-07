package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.architectures.Architecture;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.nodes.Supervisor;
import net.merayen.elastic.system.ElasticSystem;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;
import net.merayen.elastic.util.Postmaster;

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 */
public class BackendContext {
	Environment env;
	Supervisor logicnode_supervisor;
	Dispatch dispatch;

	public MessageHandler message_handler = new MessageHandler(this);

	/**
	 * Create a new default context, and starts it automatically.
	 * Remember to call end()!
	 */
	public BackendContext(ElasticSystem system, InitBackendMessage message) {
		env = Env.create(system, message);

		dispatch = new Dispatch(Architecture.LOCAL, new Dispatch.Handler() {
			@Override
			public void onMessageFromProcessor(Postmaster.Message message) {
				message_handler.queueFromProcessor(message);
			}
		});

		dispatch.launch(message);

		logicnode_supervisor = new Supervisor(env, new Supervisor.Handler() {
			@Override
			public void sendMessageToUI(net.merayen.elastic.util.Postmaster.Message message) { // Message sent from LogicNodes to the UI
				message_handler.handleFromLogicToUI(message);
			}

			@Override
			public void sendMessageToProcessor(net.merayen.elastic.util.Postmaster.Message message) { // Messages sent further into the backend, from the LogicNodes
				message_handler.handleFromLogicToProcessor(message);
			}

			@Override
			public void onProcessDone() {
				env.synchronization.push();
				env.mixer.dispatch(message.buffer_size);
			}
		});
	}

	public void start() {
		env.synchronization.start();
	}

	public Supervisor getLogicNodeList() {
		return logicnode_supervisor;
	}

	public synchronized void update() {
		message_handler.executeMessagesToBackend();
		message_handler.executeMessagesFromProcessor();
	}

	/**
	 * Stops the whole backend. Not possible to restart it.
	 */
	public void end() {
		dispatch.stop();
		env.synchronization.end();

		dispatch = null;
		env = null;
		logicnode_supervisor = null;
		message_handler = null;
	}

	public Environment getEnvironment() {
		return env;
	}
}
