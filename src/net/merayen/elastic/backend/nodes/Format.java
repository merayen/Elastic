package net.merayen.elastic.backend.nodes;

public enum Format {
	AUDIO("audio"),
	MIDI("midi");

	public String name;

	private Format(String name) {
		this.name = name;
	}
}
