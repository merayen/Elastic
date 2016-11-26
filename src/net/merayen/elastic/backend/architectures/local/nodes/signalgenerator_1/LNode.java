package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

/**
 * TODO change mode when frequency-port is connected/disconnected
 */
public class LNode extends LocalNode {
	float frequency = 1000f; // Only used in STANDALONE mode. This parameter is set in the UI
	float amplitude = 1f;  // Only used in STANDALONE mode. This parameter is set in the UI
	float offset;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onProcess(Map<String, Object> data) {
		System.out.println("Signalgenerator " + getID() + " is processing");
		for(LocalProcessor lp : getProcessors())
			lp.schedule();
	}

	@Override
	protected void onParameter(String key, Object value) {
		//System.out.printf("Signalgenerator arc %s=%s\n", key, value);
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
