package net.merayen.elastic.uinodes.list.mix_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "Mix";
	}

	@Override
	public String getDescription() {
		return "Mixes two lines";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Tool"};
	}
}
