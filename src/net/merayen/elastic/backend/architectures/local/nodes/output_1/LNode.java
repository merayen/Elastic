package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;

public class LNode extends LocalNode {
	// Tuning parameters
	private int output_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int process_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	//private int sample_rate = 44100; // TODO get this from event

	// These attributes changes if the input audio changes (we re-init the audio output device)
	private int channels = 0;

	/*private AudioOutput audio_output;

	/private HashMap<String, Number> statistics = new HashMap<String, Number>();

	private AverageStat<Integer> avg_buffer_size = new AverageStat<Integer>(100);*/

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onProcess() {
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
}
