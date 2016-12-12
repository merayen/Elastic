package net.merayen.elastic.backend.logicnodes.list.output_1;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.util.pack.PackDict;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "input";
		}});
	}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onParameterChange(NodeParameterMessage message) {
		set(message);
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
