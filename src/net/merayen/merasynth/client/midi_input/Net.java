package net.merayen.merasynth.client.midi_input;

import java.util.List;

import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.midi.devices.IMIDIDeviceAdapter;
import net.merayen.merasynth.midi.devices.MIDIScanner;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
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
public class Net extends Node {
	private final int MAX_SESSIONS = 16;
	private MidiReceiver midi_receiver = new MidiReceiver();
	private IMIDIDeviceAdapter device;
	private final ProcessorController<Processor> pc;

	public Net(Supervisor supervisor) {
		super(supervisor);
		pc = new ProcessorController<Processor>(this, Processor.class);
		initMidi();
	}

	private void initMidi() {
		List<IMIDIDeviceAdapter> devices = MIDIScanner.getDevices();
		for(IMIDIDeviceAdapter dev : devices) {
			if(
				//dev.getName().equals("Launchkey MIDI") ||
				dev.getName().equals("KEYBOARD"))
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

		if(dp instanceof AllowNewSessionsRequest) {
			startNewSessions();
			return;
		}

		if(dp.session_id != DataPacket.ALL_SESSIONS && dp.session_id != DataPacket.MAIN_SESSION && !pc.hasProcess(dp.session_id))
			// We are asked about a session we don't have. We do not create it since we are a generator.
			// This is most likely a fault on the right side of this node.
			return;

		pc.handle(port_name, dp);
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
		for(Processor p : pc.getProcessors())
			p.allow();
	}

	private void processMidiIndata() {
		MidiReceiver.MidiPacket mp;

		while((mp = midi_receiver.pop()) != null) {
			if(mp.midi[0] == MidiStatuses.KEY_DOWN) {
				spawnNewProcessor().addMidiPacket(mp);

			} else if(mp.midi[0] == MidiStatuses.KEY_UP) { // Key Up event is separate for each processor // TODO Other events like after touch should also be separated
				for(Processor p : pc.getProcessors())
					if(p.midi_note_value == mp.midi[1])
						p.addMidiPacket(mp);

			// TODO see if any other processor separate data is available, like after touch (for each tangent)

			} else { // All other data is distributed on all processes (sessions)
				for(Processor p : pc.getProcessors())
					p.addMidiPacket(mp);
			}
		}
	}

	private Processor spawnNewProcessor() {
		if(pc.activeProcesses() >= MAX_SESSIONS)
			getKillableProcessor().kill(); // Too many Processors running, need to kill a processor to process next tangent

		return pc.getProcessor(pc.createProcessor());
	}

	/*
	 * Returns the oldest session that does not have a tangent pressed.
	 * If no such is found, return the oldest active one.
	 */
	private Processor getKillableProcessor() {
		Processor oldest_inactive = null;
		Processor oldest = null;

		for(Processor p : pc.getProcessors()) {
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
