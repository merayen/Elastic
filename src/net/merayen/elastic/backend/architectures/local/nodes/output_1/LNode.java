package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {
	public LNode() {
		super(LProcessor.class);
	}

	//private Map<Integer, LocalProcessor> channels = new HashMap<>(); // Format: <channel_id, LocalProcessor()>

	final float[][] output = new float[256][]; // LProcessors writes to this directly, using their LProcessor.channel to figure out the index

	@Override
	protected void onInit() {
		
	}

	@Override
	protected void onProcess(Map<String, Object> data) {
		System.out.println("Output " + getID() + " is processing");
	}

	@Override
	protected void onParameter(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called by the processors to add output audio.
	 */
	void addData(int channel_id, float[] audio) {
		
	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		List<Integer> channels = new ArrayList<>();

		for(LocalProcessor localprocessor : getProcessors())
			channels.add(((LProcessor)localprocessor).channel_id);

		for(int channel_id = 0; channel_id < 256; channel_id++) {
			if(!channels.contains(channel_id)) {
				((LProcessor)lp).channel_id = channel_id;
				break;
			}
		}
	}

	@Override
	protected void onFinishFrame() {
		
	}
}
