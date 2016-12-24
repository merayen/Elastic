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
			name = "amplitude";
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
		//System.out.printf("Signalgenerator LogicNode value: %s: %s\n", message.key, message.value);
		set(message); // Acknowledge anyway
	}

	@Override
	protected void onConnect(String port) {
		System.out.println("Signalgenerator got connected");
	}

	@Override
	protected void onDisconnect(String port) {
		System.out.println("Signalgenerator got disconnected");
	}

	@Override
	protected void onPrepareFrame(PackDict data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishFrame(PackDict data) {
		// TODO Auto-generated method stub
		
	}
}
