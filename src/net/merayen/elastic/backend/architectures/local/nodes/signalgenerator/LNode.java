package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator;

import net.merayen.elastic.backend.architectures.local.LocalNode;

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
	protected void onProcess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onParameter(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
