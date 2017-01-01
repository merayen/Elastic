package net.merayen.elastic.backend.logicnodes.list.test_100;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.util.pack.PackDict;

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
	protected void onPrepareFrame(PackDict data) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onFinishFrame(PackDict data) {
		// TODO Auto-generated method stub
	}
}
