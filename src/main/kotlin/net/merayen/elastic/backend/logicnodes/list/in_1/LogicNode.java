package net.merayen.elastic.backend.logicnodes.list.in_1;

import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {
	@Override
	protected void onCreate() {
		setPort(Format.MIDI); // Default port. This can be configured in the UI
	}

	private void setPort(Format port_format) {
		for(String p : getPorts())
			removePort(p);

		createPort(new PortDefinition() {{
			name = "output";
			format = port_format;
			output = true;
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
