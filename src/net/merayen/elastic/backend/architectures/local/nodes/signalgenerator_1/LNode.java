package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.util.pack.PackDict;

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
	protected void onProcess(PackDict data) {
		//System.out.println("Signalgenerator " + getID() + " is processing");
		for(LocalProcessor lp : getProcessors())
			lp.schedule();
	}

	@Override
	protected void onParameter(String key, Object value) {
		//System.out.printf("Signalgenerator arc %s=%s\n", key, value);
		if(key.equals("data.frequency"))
			frequency = (float)value;

		if(key.equals("data.amplitude"))
			amplitude = (float)value;
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
