package net.merayen.elastic.backend.architectures.local.nodes.compressor_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.compressor_1.CompressorNodeOutputFrameData;
import net.merayen.elastic.backend.logicnodes.list.compressor_1.Properties;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {
	public LNode() {
		super(LProcessor.class);
	}

	double inputAmplitude = 1;
	double inputSidechainAmplitude = 1;
	double outputAmplitude = 1;
	double attack = 1;
	double release = 1;
	double threshold;
	double ratio = 1;

	private long nextUIUpdate;

	@Override
	protected void onInit() {
	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
	}

	@Override
	protected void onProcess(InputFrameData data) {
	}

	@Override
	protected void onParameter(BaseNodeProperties instance) {
		Properties data = (Properties) instance;
		Float thresholdData = data.getThreshold();
		Float attackData = data.getAttack();
		Float releaseData = data.getRelease();
		Float ratioData = data.getRatio();
		Float inputAmplitudeData = data.getInputAmplitude();
		Float inputSidechainAmplitudeData = data.getInputSidechainAmplitude();
		Float outputAmplitudeData = data.getOutputAmplitude();

		if (thresholdData != null)
			threshold = thresholdData;

		if (attackData != null)
			attack = Math.max(0.001, attackData);

		if (releaseData != null)
			release = Math.max(0.001, releaseData);

		if (ratioData != null)
			ratio = ratioData;

		if (inputAmplitudeData != null)
			inputAmplitude = inputAmplitudeData;

		if (inputSidechainAmplitudeData != null)
			inputSidechainAmplitude = inputSidechainAmplitudeData;

		if (outputAmplitudeData != null)
			outputAmplitude = outputAmplitudeData;

	}

	@Override
	protected void onFinishFrame() {
		if (nextUIUpdate < System.currentTimeMillis()) {
			nextUIUpdate = System.currentTimeMillis() + 100;
			float minAmplitude = 1;

			for (LocalProcessor lp : getProcessors())
				minAmplitude = Math.min((float) ((LProcessor) lp).amplitude, minAmplitude);

			outgoing = new CompressorNodeOutputFrameData(getID(), minAmplitude);
		}
	}

	@Override
	protected void onDestroy() {
	}
}
