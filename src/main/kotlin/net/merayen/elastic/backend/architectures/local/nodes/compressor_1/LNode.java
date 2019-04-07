package net.merayen.elastic.backend.architectures.local.nodes.compressor_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

import java.util.Map;

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
	protected void onProcess(Map<String, Object> data) {}

	@Override
	protected void onParameter(String key, Object value) {
		if(key.equals("threshold"))
			threshold = ((Number)value).floatValue();
		else if(key.equals("attack"))
			attack = Math.max(0.001, ((Number)value).floatValue());
		else if(key.equals("release"))
			release = Math.max(0.001, ((Number)value).floatValue());
		else if(key.equals("ratio"))
			ratio = ((Number)value).floatValue();
		else if(key.equals("inputAmplitude"))
			inputAmplitude = ((Number)value).floatValue();
		else if(key.equals("inputSidechainAmplitude"))
			inputSidechainAmplitude = ((Number)value).floatValue();
		else if(key.equals("outputAmplitude"))
			outputAmplitude = ((Number)value).floatValue();
	}

	@Override
	protected void onFinishFrame() {
		if(nextUIUpdate < System.currentTimeMillis()) {
			nextUIUpdate = System.currentTimeMillis() + 100;
			float minAmplitude = 1;

			for(LocalProcessor lp : getProcessors())
				minAmplitude = Math.min((float)((LProcessor)lp).amplitude, minAmplitude);

			outgoing.put("amplitude", minAmplitude);
		}
	}

	@Override
	protected void onDestroy() {}
}
