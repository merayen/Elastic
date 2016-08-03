package net.merayen.elastic.backend.architectures.local.nodes.output;

import net.merayen.elastic.backend.architectures.local.LocalNode;

public class Node extends LocalNode {
	// Tuning parameters
	private int output_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int process_buffer_size = 128; // Always try to stay minimum these many samples ahead (makes delay)
	private int sample_rate = 44100; // TODO get this from event

	// These attributes changes if the input audio changes (we re-init the audio output device)
	private int channels = 0;

	/*private AudioOutput audio_output;

	/private HashMap<String, Number> statistics = new HashMap<String, Number>();

	private AverageStat<Integer> avg_buffer_size = new AverageStat<Integer>(100);*/

	public Node() {
		super(Processor.class);
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onProcess() {
		// TODO Auto-generated method stub
		
	}
}
