package net.merayen.elastic.backend.logicnodes.list.test_100;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.NodeDataMessage;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onInit() {
		createInputPort("input");
	}

	@Override
	protected void onParameterChange(BaseNodeProperties instance) {
		updateProperties(instance);
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onData(NodeDataMessage data) {}
}
