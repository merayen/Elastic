package net.merayen.elastic.backend.architectures.local.nodes.midi_input;

import java.util.List;

import net.merayen.elastic.backend.midi.devices.IMIDIDeviceAdapter;
import net.merayen.elastic.backend.midi.devices.MIDIScanner;
import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;
import net.merayen.elastic.process.ProcessorController;

/*
 * Represents a MIDI device and output MIDI.
 * Notice that this is a live node, gathering live data. Due to this, nodes
 * requesting a-head-of-time will not receive any packets.
 * TODO Maybe be able to map multiple MIDI devices by menu
 */
public class Net extends AudioNode<Processor> {
	private MidiReceiver midi_receiver = new MidiReceiver();
	private IMIDIDeviceAdapter device;

	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
		initMidi();
	}

	private void initMidi() {
		List<IMIDIDeviceAdapter> devices = MIDIScanner.getDevices();
		for(IMIDIDeviceAdapter dev : devices) {
			System.out.printf("MIDI device available %s\n", dev.getName());
			if(
				dev.getName().equals("Launchpad Mini") ||
				dev.getName().equals("KEYBOARD") ||
				dev.getName().equals("Keystation 49e"))
			{
				System.out.printf("Using device %s\n", dev.getName());
				dev.open(midi_receiver);
				device = dev;
				return;
			}
		}
	}

	protected void onReceive(String port_name, DataPacket dp) {
		processMidiIndata();
		super.onReceive(port_name, dp);

		if(dp instanceof DataRequest) {
			if(((DataRequest)dp).allow_new_sessions && processor_controller.getProcessor(DataPacket.MAIN_SESSION) == null)
				startMainSession();

			for(Processor p : processor_controller.getProcessors())
				p.tryToSendMidi(dp.sample_count);
		}
	}

	protected void onDestroy() {
		if(device != null)
			device.close();
	}

	/**
	 * Starts all the processors (sessions) that are waiting to be started.
	 * We do this as we are only allowed to create sessions under the AllowNewSessionRequest()!
	 */
	private void startMainSession() {
		try {
			processor_controller.getProcessor(processor_controller.createProcessor(DataPacket.MAIN_SESSION));
		} catch (ProcessorController.AlreadyEndedException e) {
			throw new RuntimeException("Should not happen");
		}
	}

	private void processMidiIndata() {
		MidiReceiver.MidiPacket mp;

		while((mp = midi_receiver.pop()) != null) {
			for(Processor p : processor_controller.getProcessors())
				p.addMidiPacket(mp);
		}
	}
}
