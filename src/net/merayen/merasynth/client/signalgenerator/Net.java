package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.netlist.util.AudioNode;
import net.merayen.merasynth.process.ProcessorController;

/**
 * TODO change mode when frequency-port is connected/disconnected
 */
public class Net extends AudioNode<Processor> {
	public static enum Mode { // Hmm, do we need this? Doesn't PortManager help us decide which format is being received?
		NONE, // Default mode, where we do nothing. TODO Set to this mode when frequency-port or output-port is connected/disconnected
		MIDI,
		AUDIO,
		STANDALONE // When nothing connected to frequency-port
	}

	private Mode mode = Mode.NONE;

	public float frequency = 1000f; // Only used in STANDALONE mode. This parameter is set in the UI
	public float amplitude = 1f;  // Only used in STANDALONE mode. This parameter is set in the UI
	public int sample_rate = 44100; // TODO Set by event

	public Net(Supervisor supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);

		if(port_name.equals("output")) {
			if(dp instanceof AllowNewSessionsRequest) {
				if(!supervisor.isConnected(getPort("frequency")) && processor_controller.activeProcesses() == 0) {
					changeMode(Mode.STANDALONE);
				} else {
					// Forward AllowNewSessionsRequest as we have something connected to our frequency port
					// We can not create our own session
					send("frequency", new AllowNewSessionsRequest());
				}
			}
		} else if(port_name.equals("amp")) {
			if(dp instanceof SessionCreatedResponse)
				return; // We do not allow sessions to be created through this port, or this is a session we requested (is this right?)
		}
	}

	public double onUpdate() {
		// Doesn't process anything, unless explicitly asked for data
		return DONE;
	}

	private void changeMode(Mode new_mode) {
		if(new_mode == Mode.STANDALONE && processor_controller.getProcessor(DataPacket.MAIN_SESSION) == null) {
			try {
				processor_controller.createProcessor(DataPacket.MAIN_SESSION);
			} catch(ProcessorController.AlreadyEndedException e) {

			}
			// TODO if frequency port gets connected later on, we must destroy our session!
			// TODO request session on amp-port, if it is connected
		} else if(new_mode == Mode.AUDIO) {
			// Do nothing. Wait for a AllowNewSessionsRequest() packet that we forward on frequency-port
		} else if(new_mode == Mode.MIDI) {
			// Do nothing. Wait for a AllowNewSessionsRequest() packet that we forward on frequency-port
		}

		mode = new_mode;
		System.out.println("Running in mode " + mode);
	}

	public Mode getMode() {
		return mode;
	}

	public synchronized void setMode(Mode new_mode) {
		mode = new_mode;
	}
}
