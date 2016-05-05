package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;

/**
 * TODO change mode when frequency-port is connected/disconnected
 */
public class Net extends AudioNode<Processor> {
	float frequency = 1000f; // Only used in STANDALONE mode. This parameter is set in the UI
	float amplitude = 1f;  // Only used in STANDALONE mode. This parameter is set in the UI
	float offset;

	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);

		if(port_name.equals("output")) {
			if(dp instanceof DataRequest) {
				DataRequest dr = (DataRequest)dp;

				// Launch the main session if no connections on the left
				if(dr.allow_new_sessions && processor_controller.activeProcesses() == 0)
					if(!supervisor.isConnected(getPort("frequency")) && !supervisor.isConnected(getPort("amplitude")))
						processor_controller.createProcessor(DataPacket.MAIN_SESSION);

				for(Processor p : processor_controller.getProcessors())
					p.standalone_to_generate += dp.sample_count;

				// Forward the requests anyway, even if ports are not connected
				request("frequency", (DataRequest)dp, true);
				request("amplitude", (DataRequest)dp, false);
			}
		}

		for(Processor p : processor_controller.getProcessors())
			p.tryToGenerate();
	}

	public double onUpdate() {
		// Doesn't process anything, unless explicitly asked for data
		return DONE;
	}
}
