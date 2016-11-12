package net.merayen.elastic.backend.nodes.list.output_1;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.system.intercom.NodeParameterMessage;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "input";
		}});
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onParameterChange(NodeParameterMessage message) {
		set(message);
	}
}
