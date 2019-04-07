package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public String getDescription() {
		return "Test node for development";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Tool"};
	}
}