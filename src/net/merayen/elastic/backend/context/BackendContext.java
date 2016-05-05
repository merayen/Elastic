package net.merayen.elastic.backend.context;

import org.json.simple.JSONObject;

import net.merayen.elastic.backend.architectures.Architecture;
import net.merayen.elastic.backend.architectures.Dispatch;
import net.merayen.elastic.backend.architectures.Dispatch.Message;
import net.merayen.elastic.backend.nodes.LogicNodeList;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Serializer;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 * TODO implement the UI too
 */
public class BackendContext {
	private NetList netlist;
	private LogicNodeList logicnode_list;
	private Dispatch dispatch;
	//private final Postmaster to_backend;
	private final Postmaster from_backend;

	private BackendContext() {
		//to_backend = new Postmaster();
		from_backend = new Postmaster();
	}

	/**
	 * Create a new default context, and starts it automatically.
	 * Remember to call end()!
	 */
	public static BackendContext create() {
		BackendContext context = new BackendContext();
		context.netlist = new NetList();
		context.logicnode_list = new LogicNodeList(context.netlist);
		context.dispatch = new Dispatch(Architecture.LOCAL, new Dispatch.Handler() {
			@Override
			public void onMessage(Message message) {
				// TODO
			}
		});

		context.dispatch.launch(context.netlist, 8); // TODO 8? Nononon. Make it customizable/load it from somewhere

		return context;
	}

	/**
	 * Load from a dump.
	 */
	public static BackendContext load(JSONObject dump) {
		return null; // TODO
	}

	public void executeMessage(Postmaster.Message message) {
		System.out.printf("Backend received message of type %s\n", message.getClass().getSimpleName());

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			String node_id = logicnode_list.createNode(m.name, m.version);
			from_backend.send(new NodeCreatedMessage(node_id, m.name, m.version));

		}
	}

	public Postmaster.Message receiveFromBackend() {
		return from_backend.receive();
	}

	@SuppressWarnings("unchecked")
	public JSONObject dump() {
		JSONObject result = new JSONObject();
		result.put("netlist", Serializer.dump(netlist));
		return result;
	}

	public LogicNodeList getLogicNodeList() {
		return logicnode_list;
	}

	public NetList getNetList() {
		return netlist.copy();
	}

	public void removeNode(String node_id) {
		logicnode_list.removeNode(node_id);
	}

	public void connect(String node_a, String port_a, String node_b, String port_b) {
		netlist.connect(node_a, port_a, node_b, port_b);
	}

	public void disconnect(String node_a, String port_a, String node_b, String port_b) {
		netlist.disconnect(node_a, port_a, node_b, port_b);
	}

	public void end() {
		dispatch.stop();
	}
}
