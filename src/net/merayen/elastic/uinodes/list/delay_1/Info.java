package net.merayen.elastic.uinodes.list.delay_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "Delay";
	}

	@Override
	public String getDescription() {
		return "Delays the input signal";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Tools"};
	}
}
