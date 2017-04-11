package net.merayen.elastic.uinodes.list.signalgenerator_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "Signal generator";
	}

	@Override
	public String getDescription() {
		return "Generates audio signal";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Generators"};
	}
}