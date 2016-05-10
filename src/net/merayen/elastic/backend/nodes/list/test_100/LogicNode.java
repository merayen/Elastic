package net.merayen.elastic.backend.nodes.list.test_100;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.util.Postmaster.Message;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		BaseLogicNode.PortDefinition input = new BaseLogicNode.PortDefinition();
		input.name = "input";
		input.format = new Format[]{Format.AUDIO};
		createPort(input);
	}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
	}

	@Override
	protected void onMessageFromBackend(Message message) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}
}
