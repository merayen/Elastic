package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

public class Executor extends AbstractExecutor {
	private final NetList netlist; // Our local copy of the NetList that we build by the messages we receive
	private final Supervisor supervisor;

	Executor(NetList netlist, Supervisor supervisor) {
		this.netlist = netlist.copy();
		System.out.println("Executor started");
		this.supervisor = supervisor;
	}

	@Override
	protected void onMessage(Postmaster.Message message) {
		if(message instanceof ProcessMessage) {
			supervisor.process((ProcessMessage)message);

		} else if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;

			Node node = netlist.createNode(m.node_id);
			node.properties.put("name", m.name);
			node.properties.put("version", m.version);

			restart();

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;

			netlist.createPort(m.node_id, m.port);

			restart();

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;

			netlist.removePort(m.node_id, m.port);

			restart();

		} else if(message instanceof NodeConnectMessage) {
			NodeConnectMessage m = (NodeConnectMessage)message;
			netlist.connect(m.node_a, m.port_a, m.node_b, m.port_b);

			restart();

		} else if(message instanceof NodeDisconnectMessage) {
			NodeDisconnectMessage m = (NodeDisconnectMessage)message;
			netlist.disconnect(m.node_a, m.port_a, m.node_b, m.port_b);

			restart();

		} else if(message instanceof NodeParameterMessage) {
			// Notify all chains, that again will notify their Node
		}
	}

	/**
	 * Restarts ourself. Happens usually when we are messaged a message that
	 * requires blank sheet.
	 */
	private void restart() {
		// TODO stop the processor, analyze NetList, build the chains, and start
		System.out.println("Executor should restart TODO");
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void update() {
		/*try {
			wait();
		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}*/
	}
}
