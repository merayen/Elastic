package net.merayen.elastic.backend.architectures.local.nodes.out_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.OutputInterfaceNode;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode implements OutputInterfaceNode {
	private int channelCount = -1;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		updateChannelDistribution();
	}

	@Override
	protected void onProcess(InputFrameData data) {
		int channelCount = getParentGroupNode().getChannelCount();
		if (this.channelCount != channelCount) {
			updateChannelDistribution();
		}
	}

	@Override
	protected void onParameter(BaseNodeProperties instance) {
	}

	@Override
	protected void onFinishFrame() {
	}

	@Override
	protected void onDestroy() {
	}

	@Override
	public String getForwardPortName() {
		return "output";
	}

	@Override
	public Inlet getOutputInlet(int session_id) {
		return ((LProcessor) getProcessor(session_id)).inlet;
	}

	@Override
	public float[] getChannelDistribution(int session_id) {
		return ((LProcessor) getProcessor(session_id)).channelDistribution;
	}

	private void updateChannelDistribution() {
		int channelCount = getParent().getParentGroupNode().getChannelCount(); // Note: Gets channel count from above poly-node


		int i = 0;
		for (LocalProcessor lp : getProcessors()) {
			float distribution[] = new float[channelCount];
			distribution[i % channelCount] = 1f;
			((LProcessor) lp).channelDistribution = distribution;
			i++;
		}

		this.channelCount = channelCount;
	}
}
