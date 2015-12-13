package net.merayen.merasynth.client.output;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.KillAllSessionsRequest;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;
import net.merayen.merasynth.process.AbstractProcessor;

/*
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class Processor extends AbstractProcessor {
	private AudioFlowHelper audio_flow_helper;
	private final Net node;
	public final AudioCircularBuffer buffer = new AudioCircularBuffer(2048); // Playback buffer should not be bigger than this anyway, hopefully
	private final AudioCircularBuffer input_buffer; // Direct reference to the buffer for the "input" port

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.node = Net.class.cast(net_node); 

		audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// Won't happen
			}

			@Override
			public void onReceive(String port_name) {
				buffer.writeFromBuffer(input_buffer);
				node.notifyAudioReceived(session_id);
			}
		});
		audio_flow_helper.addInput("input");
		input_buffer = audio_flow_helper.getInputBuffer("input");
	}

	@Override
	public void handle(String port_name, DataPacket dp) {
		if(!isAlive())
			System.out.println("output feil");
		audio_flow_helper.handle(port_name, dp);

		if(dp instanceof KillAllSessionsRequest) {
			if(port_name.equals("input"))
				kill();
		}

		if(port_name.equals("input")) {
			if(dp instanceof EndSessionResponse)
				kill();
		}
	}

	public void requestAudio(int sample_count) {
		AudioRequest ar = new AudioRequest();
		ar.sample_count = sample_count;
		ar.session_id = DataPacket.ALL_SESSIONS; // We request all the processors on the left side for audio
		send("input", ar);
	}

	@Override
	public void onDestroy() {

	}
}
