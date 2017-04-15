package net.merayen.elastic.backend.logicnodes.list.mix_1;

import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.NodeParameterMessage;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		createPort(new PortDefinition() {{
			name = "a";
		}});

		createPort(new PortDefinition() {{
			name = "b";
		}});

		createPort(new PortDefinition() {{
			name = "fac";
		}});

		createPort(new PortDefinition() {{
			name = "out";
			output = true;
			format = Format.AUDIO;
		}});
	}

	@Override
	protected void onInit() {
		
	}

	@Override
	protected void onParameterChange(String key, Object value) {}

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
}
