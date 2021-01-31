package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.architectures.local.lets.*;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.PolySessions.Session;
import net.merayen.elastic.backend.midi.MidiControllers;
import net.merayen.elastic.backend.midi.MidiStatuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates sessions of is children group when a tangent is pressed.
 * Automatically kills session when tangent is depressed, AND that
 * no processor has notified us that it is active (TODO the messaging part)
 * <p>
 * TODO support multiple input and output ports
 * TODO rename input and output to in_0 and out_0 for future proofing
 */
public class LProcessor extends LocalProcessor {
	private MidiInlet input;
	private AudioOutlet output;  // The single output port of this poly-node, for now
	private final PolySessions sessions = new PolySessions();
	private final List<InterfaceNode> interfaces = new ArrayList<>();

	// MIDI states
	private short[] current_pitch;// = new short[] {MidiStatuses.PITCH_CHANGE, 0, 64};
	private short[] current_sustain = new short[]{MidiStatuses.MOD_CHANGE, MidiControllers.SUSTAIN, 0};

	@Override
	protected void onInit() {
		Inlet input = getInlet("input");

		if (input instanceof MidiInlet)
			this.input = (MidiInlet) getInlet("input");

		Outlet output = getOutlet("output");
		if (output != null) { // We made "output". It is guaranteed an AudioOutlet
			this.output = (AudioOutlet) output;
		}

		retrieveInterfaces();
	}

	@Override
	protected void onPrepare() {
		resetOutlets();
		int channelCount = getLocalNode().getParentGroupNode().getChannelCount();

		if (output != null && !sessions.isEmpty()) {
			for (int channel = 0; channel < channelCount; channel++)
				for (int i = 0; i < buffer_size; i++)
					output.audio[channel][i] = 0;
		}

		done = false;
		processedInput = false;
	}

	private boolean done = false;
	private boolean processedInput = false;

	@Override
	protected void onProcess() {
		if (done || !available())
			return;

		if (input != null && !processedInput) {
			int position = 0;
			for (Map.Entry<Integer, MidiOutlet.MidiFrame> entry : input.outlet.midi.entrySet()) { // Read midi and apply
				for (short[] n : entry.getValue()) {
					if ((n[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
						push_tangent(n[1], n[2], entry.getKey());
					} else if ((n[0] & 0b11110000) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
						release_tangent(n[1], position);
					} else if ((n[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && n[1] == MidiControllers.SUSTAIN) {
						current_sustain = n;
						sendMidi(n, position);
					} else if ((n[0] & 0b11110000) == MidiStatuses.PITCH_CHANGE) {
						current_pitch = n;
						sendMidi(n, position);
					} else {
						sendMidi(n, position); // TODO support multiple midi packets on the same sample (???)
					}
				}
			}

			// Push all input ports inside
			pushInNodes();

			processedInput = true;
		}

		// Checks if all our inner nodes has received data, otherwise, try again later
		if (!forwardOutputData())
			return; // Not all our children nodes has pushed anything yet. We try again later

		if (input != null)
			removeInactiveSessions();

		done = true;
	}

	private void push_tangent(short tangent, short velocity, int position) { // TODO support unison, and forwarding of channel number
		int unison = ((LNode) getLocalNode()).getUnison();
		for (int i = 0; i < unison; i++) {
			int spawned_session_id;
			try {
				spawned_session_id = spawnSession();
			} catch (SpawnLimitException e) {
				return; // No more voices can be spawned. XXX Should probably kill the oldest one and replace them
			}

			MidiOutlet midi_outlet = new MidiOutlet(buffer_size);
			List<OutputInterfaceNode> outnodes = new ArrayList<>();

			for (InterfaceNode in : interfaces) { // Add the children node as being connected, push() will then automatically schedule the processor
				if (in instanceof InputInterfaceNode) {
					((InputInterfaceNode) in).setForwardOutlet(spawned_session_id, midi_outlet);
				} else if (in instanceof OutputInterfaceNode) {
					// Only 1 out-node is supported. We choose one by random (UI should complain if multiple outputs)
					outnodes.add((OutputInterfaceNode) in);
				}
			}

			sessions.push(spawned_session_id, tangent, midi_outlet, outnodes.toArray(new OutputInterfaceNode[0]));

			midi_outlet.addMidi(position, new short[]{MidiStatuses.KEY_DOWN, tangent, velocity});
			if (current_pitch != null)
				midi_outlet.addMidi(position, current_pitch);

			midi_outlet.addMidi(position, current_sustain);

			//midi_outlet.push();
		}
	}

	private void release_tangent(short tangent, int position) {
		if (!sessions.isTangentDown(tangent))
			return;

		for (PolySessions.Session session : sessions.getTangentSessions(tangent)) {
			((MidiOutlet) session.input).addMidi(position, new short[]{MidiStatuses.KEY_UP, tangent, 0});

			session.active = false;
		}
	}

	/**
	 * Send MIDI to all voices.
	 */
	private void sendMidi(short[] midi, int position) {
		for (Outlet outlet : sessions.getInputOutlets()) {
			((MidiOutlet) outlet).addMidi(position, midi);
		}
	}

	@Override
	protected void onDestroy() {
	}

	/**
	 * Retrieves all interfaces.
	 */
	private void retrieveInterfaces() {
		for (LocalNode ln : getLocalNode().getChildrenNodes())
			if (ln instanceof InterfaceNode)
				interfaces.add((InterfaceNode) ln);
	}

	/**
	 * Spools all outlets (in-nodes)
	 * TODO distinguish on forward name, and session in case of deep voices
	 */
	private void pushInNodes() {
		List<Outlet> outlets = sessions.getInputOutlets();
		for (Outlet outlet : outlets)
			outlet.push();
	}

	private boolean forwardOutputData() {
		List<OutputInterfaceNode> outputNodes = getOutputNodes();

		if (outputNodes.isEmpty()) {
			if (output != null)
				output.push();

			return true; // No out-nodes. Nothing to do
		}

		if (output != null) {  // TODO optimizing this might have made it worse?
			//Map<Session, List<OutputInterfaceNode>> sessionOutputInlets = new HashMap<>();
			//Map<Session, Map<OutputInterfaceNode, List<AudioOutlet>>> cache = new HashMap<>();
			//Map<OutputInterfaceNode, Map<Session, AudioOutlet>> cache = new HashMap<>();
			//Map<OutputInterfaceNode, AudioOutlet> outputInterfaceNodeInlets = new HashMap<>(); // FIXME wrong link
			Map<Session, Map<OutputInterfaceNode, AudioOutlet>> cache = new HashMap<>();

			for (Session session : sessions.getSessions()) { // Ensure that all out-nodes has something
				Map<OutputInterfaceNode, AudioOutlet> cache1 = new HashMap<>();
				cache.put(session, cache1);

				for (OutputInterfaceNode oin : session.outnodes) {

					Inlet inlet = oin.getOutputInlet(session.id); // TODO too hot! Called like 733'000 times when profiler ran for 10 seconds!
					if (inlet instanceof AudioInlet) {
						if (!inlet.available())
							return false; // One of the out-nodes has not finished processing yet

						cache1.put(oin, (AudioOutlet)inlet.outlet);
					}
				}
			}

			float[][] out = output.audio;

			for (Map.Entry<Session, Map<OutputInterfaceNode, AudioOutlet>> x : cache.entrySet()) {
				Session session = x.getKey();
				for (Map.Entry<OutputInterfaceNode, AudioOutlet> y : x.getValue().entrySet()) {
					int sessionId = session.id;
					OutputInterfaceNode oin = y.getKey();

					float[] channelDistribution = oin.getChannelDistribution(sessionId);
					AudioOutlet outlet = (AudioOutlet)oin.getOutputInlet(sessionId).outlet;
					float[] in = outlet.audio[0];

					for (int channel = 0; channel < channelDistribution.length; channel++)
						if (channelDistribution[channel] != 0)
							for (int i = 0; i < buffer_size; i++)
								out[channel][i] += in[i] * channelDistribution[channel];
				}
			}
			output.push();
		}

		return true;
	}

	private List<OutputInterfaceNode> getOutputNodes() { // TODO implement caching
		List<OutputInterfaceNode> outnodes = new ArrayList<>();

		for (Session s : sessions.getSessions()) {
			for (OutputInterfaceNode outnode : s.outnodes) {
				if (outnode.getOutputInlet(s.id) instanceof AudioInlet)
					outnodes.add(outnode);
			}
		}

		return outnodes;
	}

	private void resetOutlets() {
		for (Outlet outlet : sessions.getInputOutlets())
			outlet.reset();
	}

	private void removeInactiveSessions() {
		List<Session> sessions_objects = new ArrayList<>(sessions.getSessions());
		List<LocalNode> lnodes = getLocalNode().getChildrenNodes();

		for (Session session : sessions_objects) {
			if (session.active)
				continue;

			boolean active = false;

			for (LocalNode ln : lnodes) {
				LocalProcessor lp = ln.getProcessor(session.id);
				if (lp instanceof SessionKeeper) {
					if (((SessionKeeper) lp).isKeepingSessionAlive()) {
						active = true;
						break;
					}
				}
			}

			if (!active) {
				sessions.removeSession(session);
				removeSession(session.id);
				//System.out.println("Poly is killing session " + session.session_id);
			}
		}

		// When there are no running sessions, clear out output-buffer so that the receiver plays silence
		// (as we won't write to it anymore, until a tangent is pressed)
		int channelCount = getLocalNode().getParentGroupNode().getChannelCount();
		if (output != null && sessions.isEmpty()) {
			for (int channel = 0; channel < channelCount; channel++) {
				float[] out = output.audio[channel]; // TODO support more than 1 channel
				for (int i = 0; i < buffer_size; i++)
					out[i] = 0;
			}
		}
	}
}
