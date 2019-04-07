package net.merayen.elastic.backend.architectures.local.nodes.in_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.InputInterfaceNode;

public class LNode extends LocalNode implements InputInterfaceNode {

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
		return "trigger";
	}

	@Override
	public void setForwardOutlet(int session_id, Outlet outlet) {
		((LProcessor)getProcessor(session_id)).setSourceOutlet(outlet);
	}

	@Override
	public void scheduleInterfaceProcessor(int session_id) {
		getProcessor(session_id).schedule();
	}
}
