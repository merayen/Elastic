package net.merayen.elastic.backend.context;

import org.json.simple.JSONObject;

import net.merayen.elastic.Info;
import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.Architecture;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.backend.nodes.Environment;
import net.merayen.elastic.backend.nodes.LogicNodeList;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Serializer;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.util.Postmaster;

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 */
public class BackendContext {
	final NetList netlist = new NetList(); // Main NetList that the UI and the architecture builds from
	final NodeProperties node_properties = new NodeProperties(netlist);

	final Environment env;

	LogicNodeList logicnode_list;
	Dispatch dispatch;
	MessageHandler message_handler = new MessageHandler(this);

	final Postmaster from_backend = new Postmaster();

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
			public void onMessageFromProcessing(Postmaster.Message message) {
				context.message_handler.fromProcessingToLogic(message);
			}
		});

		context.dispatch.launch(context.netlist, 8); // TODO 8? Make it customizable/load it from somewhere

		context.logicnode_list = new LogicNodeList(context.netlist, env, new LogicNodeList.IHandler() {
			@Override
			public void sendMessageToUI(net.merayen.elastic.util.Postmaster.Message message) { // Message sent from LogicNodes to the UI
				context.message_handler.handleFromLogicToUI(message);
			}

			@Override
			public void sendMessageToProcessor(net.merayen.elastic.util.Postmaster.Message message) { // Messages sent further into the backend, from the LogicNodes
				context.message_handler.handleFromLogicToBackend(message);
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

	public void executeMessage(Postmaster.Message message) {
		message_handler.handleFromUI(message);
	}

	public Postmaster.Message receiveFromBackend() {
		return from_backend.receive();
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("version", Info.getVersion());
		result.put("netlist", Serializer.dump(netlist));
		return result;
	}

	public LogicNodeList getLogicNodeList() {
		return logicnode_list;
	}

	public NetList getNetList() {
		return netlist.copy();
	}

	private void debug() {
		System.out.println(Serializer.dump(netlist));
	}

	public void end() {
		dispatch.stop();
	}
}
