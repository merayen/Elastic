package net.merayen.elastic.backend.logicnodes.list.test_100;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createInputPort("input");
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(BaseNodeProperties instance) {
		updateProperties(instance);
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onData(NodeDataMessage data) {}
}
