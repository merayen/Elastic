package net.merayen.elastic.backend.logicnodes;

public enum Format {
	AUDIO("audio"),
	MIDI("midi"),
	SIGNAL("signal"),

	/**
	 * Virtual port that does not transfer any data.
	 */
	VIRTUAL("virtual");

	public String name;

	Format(String name) {
		this.name = name;
	}

	public static Format get(String name) {
		for(Format f : Format.values())
			if(f.name.equals(name))
				return f;

		throw new RuntimeException("Format " + name + " not found");
	}

	public static String[] toStrings(Format[] formats) {
		String[] result = new String[formats.length];

		for(int i = 0; i < formats.length; i++)
			result[i] = formats[i].name;

		return result;
	}

	public String toString() {
		return name;
	}

	public static Format[] fromStrings(String[] formats) {
		Format[] result = new Format[formats.length];

		for(int i = 0; i < formats.length; i++)
			result[i] = get(formats[i]);

		return result;
	}
}
