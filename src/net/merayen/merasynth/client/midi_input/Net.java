package net.merayen.merasynth.client.midi_input;

import java.util.HashMap;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */

	// These attributes changes if the input audio changes (we re-init the audio output device)
	public Net(Supervisor supervisor) {
		super(supervisor);
		initMidi();
	}

	private void initMidi() {
		for(MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			MidiDevice device;

			try {
				device = MidiSystem.getMidiDevice(info);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

			System.out.printf("Device found: %s: %s, %s, %s, %s\n", info, info.getName(), info.getDescription(), info.getVendor(), info.getVersion());

			if(info.getName().equals("KEYBOARD"));
			else if(info.getName().equals("Launchpad Mini"));
			else continue;

			/*for(Transmitter x : device.getTransmitters()) {
				System.out.println("Transmitter: " + x);
				x.setReceiver(new MidiReceiver());
			}*/

			if(device.getTransmitters().size() == 0)
				continue; // No sending on this devuce TODO make a receiver node of this?
			else if(device.getTransmitters().size() == 2) {
				System.out.println("Multiple transmitters found. I don't deal with this currently.");
				continue;
			}

			Transmitter trans = null;
			try {
				trans = device.getTransmitter();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			System.out.println(trans);
			trans.setReceiver(new MidiReceiver());
			try {
				device.open();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			System.out.println("\tActivated");
		}
	}

	protected void onReceive(String port, DataPacket dp) {
		;
	}

	@Override
	protected double onUpdate() {
		return DONE;
	}

	protected void onDestroy() {
		System.out.println("Yay");
	}
}
