package net.merayen.elastic.backend.logicnodes.list.output_1;

import net.merayen.elastic.backend.nodes.BaseLogicNode;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.util.pack.FloatArray;
import net.merayen.elastic.util.pack.PackArray;
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
		PackArray pa = (PackArray)data.data.get("audio");
		FloatArray[] fa = (FloatArray[])pa.data;

		int i = 0;
		for(FloatArray channel : fa) {
			if(channel != null)
				System.out.printf("Output onFinishFrame() got channel no %d with %d samples\n", i, channel.data.length);
			i++;
		}
	}
}
