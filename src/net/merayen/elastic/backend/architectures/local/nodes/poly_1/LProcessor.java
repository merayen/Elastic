package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.PolySessions.Session;
import net.merayen.elastic.backend.midi.MidiControllers;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Creates sessions of is children group when a tangent is pressed.
 * Automatically kills session when tangent is depressed, AND that
 * no processor has notified us that it is active (TODO the messaging part)
 * 
 * TODO support multiple input and output ports
 */
public class LProcessor extends LocalProcessor {
	MidiInlet input;
	final PolySessions sessions = new PolySessions();
	final List<InterfaceNode> interfaces = new ArrayList<>();

	// MIDI states
	short[] current_pitch = new short[] {MidiStatuses.PITCH_CHANGE, 0, 64};
	short[] current_sustain = new short[] {MidiStatuses.MOD_CHANGE, MidiControllers.SUSTAIN, 0};

	@Override
	protected void onInit() {
		Inlet input = getInlet("input");

		if(input instanceof MidiInlet)
			this.input = (MidiInlet)getInlet("input");

		retrieveInterfaces();
	}

	@Override
	protected void onPrepare() {
		resetOutlets();
	}

	@Override
	protected void onProcess() {
		if(input != null) {
			int avail = input.available();
			int stop = input.outlet.written;
			if(avail > 0) {
				int position = stop - avail;
				for(short[][] m : input.outlet.midi) {
					if(m != null) {
						for(short[] n : m) {
							/*System.out.print("Poly got MIDI: ");
							for(short o : n)
								System.out.print(o + " ");
							System.out.println();*/

							if((n[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
								push_tangent(n[1], n[2], position);
							} else if((n[0] & 0b11110000) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
								release_tangent(n[1], position);
							} else if((n[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && n[1] == MidiControllers.SUSTAIN) { 
								current_sustain = n;
								sendMidi(n, position);
							} else if((n[0] & 0b11110000) == MidiStatuses.PITCH_CHANGE) {
								current_pitch = n;
								sendMidi(n, position);
							} else {
								sendMidi(n, position); // TODO support multiple midi packets on the same sample
							}
						}
						position++;
					}
				}
				spool("trigger", stop); // Ensure ports are spooled to the same position
				input.read = input.outlet.written;
			}
			if(input.read == buffer_size)
				removeInactiveSessions();
		}
	}

	private void push_tangent(short tangent, short velocity, int position) { // TODO support unison, and forwarding of channel number
		int unison = ((LNode)getLocalNode()).unison;
		for(int i = 0; i < unison; i++) {
			int spawned_session_id;
			try {
				spawned_session_id = spawnSession(0); // TODO sample_offset should not be always 0, but rather respect the offset from the midi packet
			} catch (SpawnLimitException e) {
				return; // No more voices can be spawned. XXX Should probably kill the oldest one and replace them
			}
	
			MidiOutlet outlet = new MidiOutlet(buffer_size);
	
			for(InterfaceNode in : interfaces) // Add the children node as being connected, push() will then automatically schedule the processor
				if(in instanceof InputInterfaceNode)
					((InputInterfaceNode)in).setForwardOutlet(spawned_session_id, outlet);
	
			sessions.push(spawned_session_id, tangent, outlet);
	
			outlet.midi[position] = new short[][] {{MidiStatuses.KEY_DOWN, tangent, velocity}, current_pitch, current_sustain};
			outlet.written = position;
			outlet.push();
		}
	}

	private void release_tangent(short tangent, int position) {
		if(!sessions.isTangentDown(tangent))
			return;

		for(PolySessions.Session session : sessions.getTangentSessions(tangent)) {
			((MidiOutlet)session.outlet).midi[position] = new short[][] {{MidiStatuses.KEY_UP, tangent, 0}}; // Send KEY UP to all the sessions
			session.outlet.push();
			session.active = false;
		}

		//System.out.printf("Removed tangent %d with session_id %d\n", tangent, session.session_id);
	}

	/**
	 * Send MIDI to all voices.
	 */
	private void sendMidi(short[] midi, int position) {
		for(Outlet outlet : sessions.getOutlets()) {
			short[][][] out_midi = ((MidiOutlet)outlet).midi;
			if(out_midi[position] == null) {
				out_midi[position] = new short[][] {midi};
			} else { // This is lol, but will probably not happen that much, so... w/e
				short[][] new_midis = new short[out_midi[position].length + 1][];
				System.arraycopy(out_midi[position], 0, new_midis, 0, out_midi[position].length);
				new_midis[new_midis.length - 1] = midi;
				out_midi[position] = new_midis;
			}
			outlet.push();
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

	/**
	 * Retrieves all interfaces.
	 */
	private void retrieveInterfaces() {
		for(LocalNode ln : getLocalNode().getChildrenNodes())
			if(ln instanceof InterfaceNode)
				interfaces.add((InterfaceNode)ln);
	}

	/**
	 * Spools all outlets to a certain position.
	 * TODO distinguish on forward name
	 */
	private void spool(String forward_port, int position) {
		for(Outlet outlet : sessions.getOutlets()) {
			if(outlet.written < position) {
				outlet.written = position;
				outlet.push();
			}
		}
	}

	private void resetOutlets() {
		for(Outlet outlet : sessions.getOutlets()) {
			short[][][] midi = ((MidiOutlet)outlet).midi;
			for(int i = 0; i < buffer_size; i++)
				midi[i] = null;
			outlet.reset(0);
		}
	}

	private void removeInactiveSessions() {
		List<Session> sessions_objects = new ArrayList<>(sessions.getSessions());
		List<LocalNode> lnodes = getLocalNode().getChildrenNodes();

		for(Session session : sessions_objects) {
			if(session.active)
				continue;

			boolean active = false;

			for(LocalNode ln : lnodes) {
				LocalProcessor lp = ln.getProcessor(session.session_id);
				if(lp instanceof SessionKeeper) {
					if(((SessionKeeper)lp).isKeepingSessionAlive()) {
						active = true;
						break;
					}
				}
			}

			if(!active) {
				sessions.removeSession(session);
				removeSession(session.session_id);
				//System.out.println("Poly is killing session " + session.session_id);
			}
		}
	}
}
