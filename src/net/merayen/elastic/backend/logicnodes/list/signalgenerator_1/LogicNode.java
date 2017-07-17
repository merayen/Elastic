package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1;

import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {
	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "frequency";
		}});

		createPort(new BaseLogicNode.PortDefinition() {{
			name = "output";
			format = Format.AUDIO;
			output = true;
		}});

		set("data.frequency", 440.0f);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(String key, Object value) { // Parameter change from UI
		set(key, value); // Acknowledge anyway
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {}

	@Override
	protected void onRemove() {}

	@Override
	protected void onData(Map<String, Object> data) {}
}
