package net.merayen.elastic.backend.logicnodes.list.test_100;

import java.util.Map;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		BaseLogicNode.PortDefinition input = new BaseLogicNode.PortDefinition();
		input.name = "input";
		createPort(input);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onData(Object data) {}
}
