package net.merayen.elastic.backend.logicnodes.list.in_1;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.system.intercom.OutputFrameData;

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
