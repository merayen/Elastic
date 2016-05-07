package net.merayen.elastic.backend.nodes.list.signalgenerator_1;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.util.Postmaster.Message;

public class LogicNode extends BaseLogicNode {
	@Override
	protected void onCreate() {
		System.out.println("Signal generator created");
	}

	@Override
	protected void onMessageFromUI(Message message) {
		if(message instanceof NodeParameterMessage) {
			System.out.printf("Signalgenerator got parameter: %s: %s\n", ((NodeParameterMessage) message).key, ((NodeParameterMessage) message).value);
			sendMessageToBackend(message); // TODO inspect and control message?
			sendMessageToUI(message); // Acknowledge change
		}
	}

	@Override
	protected void onMessageFromBackend(Message message) {

	}

	@Override
	protected void onConnect(String port) {
		System.out.println("Signalgenerator got connected");
	}

	@Override
	protected void onDisconnect(String port) {
		System.out.println("Signalgenerator got disconnected");
	}
}
