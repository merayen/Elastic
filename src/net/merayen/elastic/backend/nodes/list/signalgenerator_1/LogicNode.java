package net.merayen.elastic.backend.nodes.list.signalgenerator_1;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.system.intercom.NodeParameterMessage;

public class LogicNode extends BaseLogicNode {
	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "frequency";
		}});

		createPort(new BaseLogicNode.PortDefinition() {{
			name = "amplitude";
		}});

		createPort(new BaseLogicNode.PortDefinition() {{
			name = "output";
			format = Format.AUDIO;
			output = true;
		}});
	}

	@Override
	protected void onParameterChange(NodeParameterMessage message) { // Parameter change from UI
		//System.out.printf("Signalgenerator LogicNode value: %s: %s\n", message.key, message.value);
		set(message); // Acknowledge anyway
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
