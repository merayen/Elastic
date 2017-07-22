package net.merayen.elastic.backend.architectures.local.nodes.out_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.OutputInterfaceNode;

public class LNode extends LocalNode implements OutputInterfaceNode {
	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(Map<String, Object> data) {}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}

	@Override
	public String getForwardPortName() {
		return "output";
	}

	@Override
	public Inlet getForwardInlet(int session_id) {
		return getProcessor(session_id).getInlet("input");
	}
}
