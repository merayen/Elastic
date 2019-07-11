package net.merayen.elastic.backend.architectures.local.nodes.compressor_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.compressor_1.CompressorNodeOutputFrameData;
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
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(String key, Object value) {
		switch (key) {
			case "threshold":
				threshold = ((Number) value).floatValue();
				break;
			case "attack":
				attack = Math.max(0.001, ((Number) value).floatValue());
				break;
			case "release":
				release = Math.max(0.001, ((Number) value).floatValue());
				break;
			case "ratio":
				ratio = ((Number) value).floatValue();
				break;
			case "inputAmplitude":
				inputAmplitude = ((Number) value).floatValue();
				break;
			case "inputSidechainAmplitude":
				inputSidechainAmplitude = ((Number) value).floatValue();
				break;
			case "outputAmplitude":
				outputAmplitude = ((Number) value).floatValue();
				break;
		}
	}

	@Override
	protected void onFinishFrame() {
		if(nextUIUpdate < System.currentTimeMillis()) {
			nextUIUpdate = System.currentTimeMillis() + 100;
			float minAmplitude = 1;

			for(LocalProcessor lp : getProcessors())
				minAmplitude = Math.min((float)((LProcessor)lp).amplitude, minAmplitude);

			outgoing = new CompressorNodeOutputFrameData(getID(), minAmplitude);
		}
	}

	@Override
	protected void onDestroy() {}
}
