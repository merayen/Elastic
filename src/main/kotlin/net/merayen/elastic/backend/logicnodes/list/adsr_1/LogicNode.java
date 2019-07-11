package net.merayen.elastic.backend.logicnodes.list.adsr_1;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.system.intercom.OutputFrameData;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createPort(new PortDefinition() {{
			name = "input";
		}});

		createPort(new PortDefinition() {{
			name = "output";
			output = true;
			format = Format.MIDI;
		}});
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(String key, Object value) {
		set(key, value);
	}

	@Override
	protected void onData(Object data) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onFinishFrame(OutputFrameData data) {}
}
