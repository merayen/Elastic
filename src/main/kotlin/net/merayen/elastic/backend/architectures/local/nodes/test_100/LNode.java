package net.merayen.elastic.backend.architectures.local.nodes.test_100;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onProcess(InputFrameData data) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onParameter(BaseNodeData instance) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishFrame() {
		// TODO Auto-generated method stub

	}
}
