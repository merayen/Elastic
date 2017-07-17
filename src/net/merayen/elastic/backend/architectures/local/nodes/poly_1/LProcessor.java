package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Creates sessions of is children group when a tangent is pressed.
 * Automatically kills session when tangent is depressed, AND that
 * no processor has notified us that it is active (TODO the messaging part)
 */
public class LProcessor extends LocalProcessor {
	private static class Session {
		final short tangent;
		final short velocity;
		final int session_id;
		final Set<LocalProcessor> keep_alive = new HashSet<>(); // Processors that keeps the session alive.

		Session(short tangent, short velocity, int session_id) {
			this.tangent = tangent;
			this.velocity = velocity;
			this.session_id = session_id;
		}
	}

	MidiInlet input;
	Map<Short, Session> keys_down = new HashMap<>();

	@Override
	protected void onInit() {
		if(getInlet("input") instanceof MidiInlet)
			input = (MidiInlet)getInlet("input");
	}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		if(input != null) {
			int avail = input.available();
			if(avail > 0) {
				for(short[][] m : input.outlet.midi) {
					if(m != null) {
						for(short[] n : m) {
							System.out.print("Poly got MIDI: ");
							for(short o : n)
								System.out.print(o + " ");
							System.out.println();

							if((n[0] & 0b11110000) == MidiStatuses.KEY_DOWN)
								push_tangent(n[1], n[2]);
							else if((n[0] & 0b11110000) == MidiStatuses.KEY_UP) // Also detect KEY_DOWN with 0 velocity!
								release_tangent(n[1]);
						}
					}
				}
			}
			input.read = input.outlet.written;
		}
	}

	private void push_tangent(short tangent, short velocity) {
		if(keys_down.containsKey(tangent))
			return;

		int spawned_session_id = spawnSession(0); // TODO sample_offset should not be always 0, but rather respect the offset from the midi packet
		keys_down.put(tangent, new Session(tangent, velocity, spawned_session_id));
		System.out.printf("Added tangent %d with session_id %d\n", tangent, spawned_session_id);
	}

	private void release_tangent(short tangent) {
		if(!keys_down.containsKey(tangent))
			return;

		Session session = keys_down.remove(tangent);
		removeSession(session.session_id);
		System.out.printf("Removed tangent %d with session_id %d\n", tangent, session.session_id);
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
