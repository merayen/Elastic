package net.merayen.elastic.uinodes.list.midi_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Midi";
	}

	@Override
	public String getDescription() {
		return "Midi notes";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Generators"};
	}
}
