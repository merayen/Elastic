package net.merayen.elastic.backend.context;

import org.json.simple.JSONObject;

import net.merayen.elastic.Info;
import net.merayen.elastic.backend.architectures.Architecture;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.nodes.Supervisor;
import net.merayen.elastic.netlist.Serializer;
import net.merayen.elastic.util.Postmaster;

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 */
public class BackendContext {
	final Environment env;

	Supervisor logicnode_supervisor;
	Dispatch dispatch;

	public MessageHandler message_handler = new MessageHandler(this);

	private BackendContext(Environment env) {
		this.env = env;
	}

	/**
	 * Create a new default context, and starts it automatically.
	 * Remember to call end()!
	 */
	public static BackendContext create(Environment env) {
		BackendContext context = new BackendContext(env);
		context.dispatch = new Dispatch(Architecture.LOCAL, new Dispatch.Handler() {
			@Override
			public void onMessageFromProcessor(Postmaster.Message message) {
				context.message_handler.queueFromProcessor(message);
			}
		});

		context.dispatch.launch(8); // TODO 8? Make it customizable/load it from somewhere

		context.logicnode_supervisor = new Supervisor(env, new Supervisor.Handler() {
			@Override
			public void sendMessageToUI(net.merayen.elastic.util.Postmaster.Message message) { // Message sent from LogicNodes to the UI
				context.message_handler.handleFromLogicToUI(message);
			}

			@Override
			public void sendMessageToProcessor(net.merayen.elastic.util.Postmaster.Message message) { // Messages sent further into the backend, from the LogicNodes
				context.message_handler.handleFromLogicToProcessor(message);
			}

			@Override
			public void onProcessDone() {
				// TODO push the mixer or whatever
				env.synchronization.push();
				System.out.println("A frame has been processed.");
			}
		});

		return context;
	}

	/**
	 * Load from a dump.
	 */
	public static BackendContext load(JSONObject dump) {
		return null; // TODO
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("version", Info.getVersion());
		result.put("netlist", Serializer.dump(logicnode_supervisor.getNetList()));
		return result;
	}

	public Supervisor getLogicNodeList() {
		return logicnode_supervisor;
	}

	public void update() {
		message_handler.executeMessagesToBackend();
		message_handler.executeMessagesFromProcessor();
	}

	public void end() {
		dispatch.stop();
	}
}
