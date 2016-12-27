package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.util.pack.PackDict;
import net.merayen.elastic.util.pack.FloatArray;
import net.merayen.elastic.util.pack.PackArray;

public class LNode extends LocalNode {
	public LNode() {
		super(LProcessor.class);
	}

	//private Map<Integer, LocalProcessor> channels = new HashMap<>(); // Format: <channel_id, LocalProcessor()>

	// TODO null-ify channels that gets removed
	final float[][] output = new float[256][]; // LProcessors writes to this directly, using their LProcessor.channel to figure out the index

	@Override
	protected void onInit() {

	}

	@Override
	protected void onProcess(PackDict data) {
		//System.out.println("Output " + getID() + " is processing");
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
		int channel_count = countChannels();
		PackArray channels = new PackArray();
		FloatArray[] fa;
		channels.data = fa = new FloatArray[channel_count];

		float[] amplitude = new float[channel_count];

		for(int channel_id = 0; channel_id < channel_count; channel_id++) {
			if(output[channel_id] != null) {

				float[] arr = new float[output[channel_id].length];

				System.arraycopy(output[channel_id], 0, arr, 0, arr.length);

				fa[channel_id] = new FloatArray(arr);

				// Measure max amplitude
				for(float v : output[channel_id])
					if(amplitude[channel_id] < v)
						amplitude[channel_id] = v;
			}
		}

		outgoing.data.put("audio", channels);

		if(channel_count > 0)
			outgoing.data.put("vu", new FloatArray(amplitude));
	}

	private int countChannels() {
		int count = 0;
		for(int i = 0; i < output.length; i++)
			if(output[i] != null)
				count = i + 1;

		return count;
	}
}