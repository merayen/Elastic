package net.merayen.elastic.backend.architectures.local.nodes.out_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.OutputInterfaceNode;

public class LNode extends LocalNode implements OutputInterfaceNode {
	private int channelCount;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		((LProcessor)lp).channelDistribution = channelCount++ % 2 == 0 ? new float[]{1,0} : new float[]{0,1};
	}

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
	public Inlet getOutputInlet(int session_id) {
		return ((LProcessor)getProcessor(session_id)).inlet;
	}

	@Override
	public float[] getChannelDistribution(int session_id) {
		return ((LProcessor)getProcessor(session_id)).channelDistribution;
	}
}
