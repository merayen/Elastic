package net.merayen.elastic.backend.logicnodes.list.midi_1;

import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createPort(new PortDefinition() {{
			name = "in";
		}});

		createPort(new PortDefinition() {{
			name = "out";
			output = true;
			format = Format.AUDIO;
		}});
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onParameterChange(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onConnect(String port) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDisconnect(String port) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onRemove() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPrepareFrame(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishFrame(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

}
