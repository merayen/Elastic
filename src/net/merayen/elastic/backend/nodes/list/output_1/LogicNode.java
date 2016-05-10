package net.merayen.elastic.backend.nodes.list.output_1;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.util.Postmaster.Message;

public class LogicNode extends BaseLogicNode {

	@Override
	protected void onCreate() {
		createPort(new BaseLogicNode.PortDefinition() {{
			name = "input";
			format = new Format[]{Format.AUDIO};
		}});
	}

	@Override
	protected void onMessageFromBackend(Message message) {}

	@Override
	protected void onConnect(String port) {}

	@Override
	protected void onDisconnect(String port) {}

	@Override
	protected void onParameterChange(String key, Object value) {}
}
