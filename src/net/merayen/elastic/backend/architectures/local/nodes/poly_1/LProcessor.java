package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.architectures.local.lets.*;
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
 * TODO rename input and output to in_0 and out_0 for future proofing
 */
public class LProcessor extends LocalProcessor {
	private MidiInlet input;
	private AudioOutlet output;
	private final PolySessions sessions = new PolySessions();
	private final List<InterfaceNode> interfaces = new ArrayList<>();

	// MIDI states
	private short[] current_pitch = new short[] {MidiStatuses.PITCH_CHANGE, 0, 64};
	private short[] current_sustain = new short[] {MidiStatuses.MOD_CHANGE, MidiControllers.SUSTAIN, 0};

	/**
	 * How many samples that has been processed.
	 * Used to compare with inlets so that we can decide if we can process from the Inlets.
	 */
	private int samples_processed;

	@Override
	protected void onInit() {
		Inlet input = getInlet("input");

		if(input instanceof MidiInlet)
			this.input = (MidiInlet)getInlet("input");

		Outlet output = getOutlet("output");
		if(output != null) { // We made "output". It is guaranteed an AudioOutlet
			this.output = (AudioOutlet) output;
			this.output.setChannelCount(1); // TODO support more than 1 channel
		}

		retrieveInterfaces();
	}

	@Override
	protected void onPrepare() {
		resetOutlets();
		samples_processed = 0;
		if(output != null && !sessions.isEmpty()) {
			for (int i = 0; i < buffer_size; i++)
				output.audio[0][i] = 0;
		}
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
		}

		forwardOutputData();

		if(input != null && input.read == buffer_size)
			removeInactiveSessions();
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
	
			MidiOutlet midi_outlet = new MidiOutlet(buffer_size);
			List<OutputInterfaceNode> outnodes = new ArrayList<>();
	
			for(InterfaceNode in : interfaces) { // Add the children node as being connected, push() will then automatically schedule the processor
				if (in instanceof InputInterfaceNode) {
					((InputInterfaceNode) in).setForwardOutlet(spawned_session_id, midi_outlet);
				} else if (in instanceof OutputInterfaceNode) {
					// Only 1 out-node is supported. We choose one by random (UI should complain if multiple outputs)
					outnodes.add((OutputInterfaceNode) in);
				}
			}

			sessions.push(spawned_session_id, tangent, midi_outlet, outnodes.toArray(new OutputInterfaceNode[outnodes.size()]));
	
			midi_outlet.midi[position] = new short[][] {{MidiStatuses.KEY_DOWN, tangent, velocity}, current_pitch, current_sustain};
			midi_outlet.written = position;
			midi_outlet.push();
		}
	}

	private void release_tangent(short tangent, int position) {
		if(!sessions.isTangentDown(tangent))
			return;

		for(PolySessions.Session session : sessions.getTangentSessions(tangent)) {
			((MidiOutlet)session.input).midi[position] = new short[][] {{MidiStatuses.KEY_UP, tangent, 0}}; // Send KEY UP to all the sessions
			session.input.push();
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
	 * TODO distinguish on forward name, and session in case of deep voices
	 */
	private void spool(String forward_port, int position) {
		for(Outlet outlet : sessions.getOutlets()) {
			if(outlet.written < position) {
				outlet.written = position;
				outlet.push();
			}
		}
	}

	private void forwardOutputData() {
		List<AudioInlet> audioInlets = getAudioInlets();

		if(audioInlets.isEmpty()) {
			Outlet output_outlet = getOutlet("output");
			if(output_outlet != null) {
				output_outlet.written = buffer_size;
				output_outlet.push();
			}

			return; // No out-nodes. Nothing to do
		}

		int sample_position = Integer.MAX_VALUE;
		for(AudioInlet ai : audioInlets) {
			if (ai.outlet.written < sample_position)
				sample_position = ai.outlet.written;

			ai.read = ai.outlet.written; // We do spool the inlet always, as we keep track of position ourselves with samples_processed.
		}

		if(sample_position <= samples_processed)
			return; // There is not new data on all inlet voices, can not process. Come back later.

		if(output != null) { // TODO mix to channels
			float[] out = output.audio[0];

			for(AudioInlet ai : audioInlets) {
				if(ai.outlet.getChannelCount() != 1) // TODO support multiple channels inside poly-node
					throw new RuntimeException("Poly node does not support multiple channels inside. TODO");

				float[] in = ai.outlet.audio[0];

				for(int i = samples_processed; i < sample_position; i++)
					out[i] += in[i];
			}

			output.written = sample_position;
			output.push();
		}

		samples_processed = sample_position;
	}

	private List<AudioInlet> getAudioInlets() { // TODO implement caching
		List<AudioInlet> audioInlets = new ArrayList<>();

		for(Session s : sessions.getSessions()) {
			AudioInlet audioInlet = null;
			for(OutputInterfaceNode outnode : s.outnodes) {
				Inlet inlet = outnode.getOutputInlet(s.session_id);
				if(inlet instanceof AudioInlet)
					audioInlets.add((AudioInlet) inlet);
			}
		}

		//System.out.println(audioInlets.size() + " " + sessions.getSessions().size());

		return audioInlets;
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

		// When there are no running sessions, clear out output-buffer so that the receiver plays silence
		// (as we won't write to it anymore, until a tangent is pressed)
		if(output != null && sessions.isEmpty()) {
			float[] out = output.audio[0]; // TODO support more than 1 channel
			for(int i = 0; i < buffer_size; i++)
				out[i] = 0;
		}
	}
}
