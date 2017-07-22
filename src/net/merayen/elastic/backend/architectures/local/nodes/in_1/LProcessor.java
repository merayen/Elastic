package net.merayen.elastic.backend.architectures.local.nodes.in_1;

import java.lang.reflect.InvocationTargetException;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.FormatMaps;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * TODO support more than just MIDI. Will probably crash and explode if anything else is given to it.
 */
public class LProcessor extends LocalProcessor {
	private Inlet input; // Manual inlet that maps the parent node's input

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {
		if(input != null) {
			// Need to do this manually, as this inlet is not automatically handled)
			input.reset(0);
		}
	}

	@Override
	protected void onProcess() {
		Outlet output = getOutlet("output");
		if(output != null && input.outlet.written > input.read) {
			MidiOutlet midi_output = (MidiOutlet)output;
			MidiOutlet source = (MidiOutlet)input.outlet;

			int avail = input.available();
			for(; input.read < source.written; input.read++)
				midi_output.midi[input.read] = source.midi[input.read];

			output.written += avail;
			output.push();
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

	/**
	 * Set by the parent node.
	 */
	void setSourceOutlet(Outlet outlet) {
		if(outlet == null) return;

		if(input == null) {
			try { // Wrap the outlet with an inlet
				input = FormatMaps.inlet_formats.get(outlet.getFormat()).getConstructor(Outlet.class).newInstance(outlet);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e); // Lol, Java
			}

			outlet.connected_processors.add(this); // Schedules us whenever the source pushes data ( push() )

			// Tries to figure out where to begin from our output port. Should be safe as all wiring is done before onPrepare() is called
			input.reset( getOutlet("output") != null ? getOutlet("output").written : 0 );
		}
	}

	public Inlet getInput() {
		return input;
	}
}
