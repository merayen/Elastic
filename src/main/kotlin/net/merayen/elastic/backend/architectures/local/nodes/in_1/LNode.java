package net.merayen.elastic.backend.architectures.local.nodes.in_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.InputInterfaceNode;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode implements InputInterfaceNode {

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

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
