package net.merayen.elastic.backend.architectures.local.nodes.voices;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.datapacket.MidiResponse;
import net.merayen.elastic.netlist.util.AudioNode;

public class Net extends AudioNode<Processor> {
	int voices = 16;

	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);
		if(port_name.equals("output")) {
			if(dp instanceof DataRequest) {
				request("input", (DataRequest)dp, true);
			}
		}

		if(port_name.equals("input")) {
			if(dp instanceof MidiResponse) {
				
			}
		}
	}

	@Override
	protected double onUpdate() {
		return DONE;
	}

	public void changeVoiceCount(int voices) {
		this.voices = voices;
	}

	/**
	 * Called by InputProcessor to create an output VoiceProcessor
	 */
	VoiceProcessor createVoiceProcessor() {
		return (VoiceProcessor)processor_controller.getProcessor(processor_controller.createProcessor());
	}

	@Override
	protected Class<? extends Processor> onSelectProcessor(long session_id) {
		if(session_id == DataPacket.MAIN_SESSION)
			return InputProcessor.class;
		else
			return VoiceProcessor.class;
			
	}
}
