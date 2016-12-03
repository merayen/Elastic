package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.util.pack.Dict;
import net.merayen.elastic.util.pack.FloatArray;
import net.merayen.elastic.util.pack.PackArray;

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
	protected void onProcess(Dict data) {
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
		PackArray channels = new PackArray();
		channels.data = new FloatArray[output.length];

		for(int channel_id = 0; channel_id < output.length; channel_id++)
			if(output[channel_id] != null)
				((FloatArray[])channels.data)[channel_id] = new FloatArray(output[channel_id]);

		outgoing.data.put("audio", channels);
	}
}