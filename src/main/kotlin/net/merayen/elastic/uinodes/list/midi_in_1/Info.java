package net.merayen.elastic.uinodes.list.midi_in_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "MIDI input";
	}

	@Override
	public String getDescription() {
		return "MIDI from devices";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Interface"};
	}
}