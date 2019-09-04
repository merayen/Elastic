package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.adsr_1.Properties;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {
	float attack, decay, sustain, release;

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
	protected void onParameter(BaseNodeProperties instance) {
		Properties data = (Properties)instance;
		Float attackData = data.getAttack();
		Float decayData = data.getDecay();
		Float sustainData = data.getSustain();
		Float releaseData = data.getRelease();

		if(attackData != null)
			attack = attackData;

		if(decayData != null)
			decay = decayData;

		if(sustainData != null)
			sustain = sustainData;

		if(releaseData != null)
			release = releaseData;
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
