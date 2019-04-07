package net.merayen.elastic.backend.architectures.local.lets;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;

public class FormatMaps {
	public static final Map<Format, Class<? extends Outlet>> outlet_formats = new HashMap<>();
	public static final Map<Format, Class<? extends Inlet>> inlet_formats = new HashMap<>();

	static {
		outlet_formats.put(Format.AUDIO, AudioOutlet.class);
		outlet_formats.put(Format.MIDI, MidiOutlet.class);
		outlet_formats.put(Format.SIGNAL, SignalOutlet.class);

		inlet_formats.put(Format.AUDIO, AudioInlet.class);
		inlet_formats.put(Format.MIDI, MidiInlet.class);
		inlet_formats.put(Format.SIGNAL, SignalInlet.class);
	}

	private FormatMaps() {}
}
