package net.merayen.merasynth.client.midi_input;

import java.util.List;

import net.merayen.merasynth.midi.devices.IMIDIDeviceAdapter;
import net.merayen.merasynth.midi.devices.MIDIScanner;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.MidiRequest;

/*
 * Represents a MIDI device and output MIDI.
 * Notice that this is a live node, gathering live data. Due to this, nodes
 * requesting a-head-of-time will not receive any packets (request is ignored).
 * TODO Maybe be able to map multiple MIDI devices.
 * TODO cache MIDI packets, if output is connected.
 */
public class Net extends Node {
	private MidiReceiver midi_receiver = new MidiReceiver();
	private IMIDIDeviceAdapter device;

	public Net(Supervisor supervisor) {
		super(supervisor);
		initMidi();
	}

	private void initMidi() {
		List<IMIDIDeviceAdapter> devices = MIDIScanner.getDevices();
		for(IMIDIDeviceAdapter dev : devices) {
			System.out.println(dev.getName());
			if(dev.getName().equals("KEYBOARD")) {
				dev.open(midi_receiver);
				device = dev;
			}
		}
	}

	protected void onReceive(String port_name, DataPacket dp) {
		if(port_name.equals("output") && dp instanceof MidiRequest) {
			MidiRequest mr = (MidiRequest)dp;
			send("output", midi_receiver.retrieve(mr.sample_count));
		}
	}

	protected void onDestroy() {
		if(device != null)
			device.close();
	}
}
