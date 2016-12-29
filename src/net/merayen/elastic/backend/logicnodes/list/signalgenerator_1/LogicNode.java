package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.util.pack.PackDict;

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
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onParameterChange(NodeParameterMessage message) { // Parameter change from UI
		set(message); // Acknowledge anyway
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onPrepareFrame(PackDict data) {}

	@Override
	protected void onFinishFrame(PackDict data) {}
}
