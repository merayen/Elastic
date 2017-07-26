package net.merayen.elastic.backend.logicnodes.list.adsr_1;

import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

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
	protected void onData(Map<String, Object> data) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {}
}
