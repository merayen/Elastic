package net.merayen.elastic.backend.architectures.local.lets;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.backend.logicnodes.Format;

public class FormatMaps {
	public static final Map<Format, Class<? extends Outlet>> outlet_formats = new HashMap<>();

	static {
		outlet_formats.put(Format.AUDIO, AudioOutlet.class);
		outlet_formats.put(Format.MIDI, MidiOutlet.class);
	}

	private FormatMaps() {}
}
