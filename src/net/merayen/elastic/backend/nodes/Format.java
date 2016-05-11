package net.merayen.elastic.backend.nodes;

import java.util.ArrayList;
import java.util.List;

public enum Format {
	AUDIO("audio"),
	MIDI("midi");

	public String name;

	private Format(String name) {
		this.name = name;
	}

	public Format get(String name) {
		for(Format f : Format.values())
			if(f.name.equals(name))
				return f;

		return null;
	}

	public static List<String> toStrings(Format[] formats) {
		List<String> result = new ArrayList<>();

		for(Format f : formats)
			result.add(f.name);

		return result;
	}
}
