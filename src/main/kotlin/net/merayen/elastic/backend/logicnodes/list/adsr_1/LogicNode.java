package net.merayen.elastic.backend.logicnodes.list.adsr_1;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onInit() {
		createInputPort("input");
		createOutputPort("output", Format.MIDI);
	}

	@Override
	protected void onParameterChange(BaseNodeProperties instance) {
		updateProperties(instance);
	}

	@Override
	protected void onData(NodeDataMessage data) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}
}
