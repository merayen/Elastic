package net.merayen.merasynth.client.midi_input;

import java.util.List;

import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.midi.devices.IMIDIDeviceAdapter;
import net.merayen.merasynth.midi.devices.MIDIScanner;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.util.AudioNode;
import net.merayen.merasynth.process.AbstractProcessor;
import net.merayen.merasynth.process.ProcessorController;

/*
 * Represents a MIDI device and output MIDI.
 * Notice that this is a live node, gathering live data. Due to this, nodes
 * requesting a-head-of-time will not receive any packets.
 * Node kills session when its MAX_SESSIONS is reached. This will let processors
 * live infinitive until new processors are created.
 * Receiving right node needs to end its session (rightward), like when the tangent goes up or
 * by some other circumstances.
 * TODO Maybe be able to map multiple MIDI devices.
 */
public class Net extends AudioNode<Processor> {
	private final int MAX_SESSIONS = 16;
	private MidiReceiver midi_receiver = new MidiReceiver();
	private IMIDIDeviceAdapter device;

	public Net(Supervisor supervisor) {
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

		if(dp instanceof AllowNewSessionsRequest)
			startNewSessions();
	}

	protected void onDestroy() {
		if(device != null)
			device.close();
	}

	/*
	 * Starts all the processors (sessions) that are waiting to be started.
	 * We do this as we are only allowed to create sessions under the AllowNewSessionRequest()!
	 */
	private void startNewSessions() {
		for(Processor p : processor_controller.getProcessors())
			p.allow();
	}

	private void processMidiIndata() {
		MidiReceiver.MidiPacket mp;

		while((mp = midi_receiver.pop()) != null) {
			if(mp.midi[0] == MidiStatuses.KEY_DOWN && mp.midi[2] > 0) // Tangent down, and we require a value. Zero value will be treated by KEY_UP by processor
				spawnNewProcessor().addMidiPacket(mp);
			else
				for(Processor p : processor_controller.getProcessors()) // All other data is distributed on all processes (sessions)
					p.addMidiPacket(mp);
		}
	}

	private Processor spawnNewProcessor() {
		if(processor_controller.activeProcesses() >= MAX_SESSIONS)
			getKillableProcessor().terminate(); // Too many Processors running, need to kill a processor to process next tangent. TODO Make sure we end the session graceuflly (informing right nodes)

		return processor_controller.getProcessor(processor_controller.createProcessor());
	}

	/*
	 * Returns the oldest session that does not have a tangent pressed.
	 * If no such is found, return the oldest active one.
	 */
	private Processor getKillableProcessor() {
		Processor oldest_inactive = null;
		Processor oldest = null;

		for(Processor p : processor_controller.getProcessors()) {
			if(oldest == null || p.time_created < oldest.time_created) {
				oldest = p;
			}
			if(oldest_inactive == null || p.time_created < oldest_inactive.time_created) {
				if(!p.isTangentDown())
					oldest_inactive = p;
			}
		}

		if(oldest_inactive != null)
			return oldest_inactive; // Return a processor that does not have a tangent down

		return oldest; // All tangents down, so... We kill the oldest one 
	}
}
