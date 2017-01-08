package net.merayen.elastic.uinodes.list.output_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {
		return "Output";
	}

	@Override
	public String getDescription() {
		return "Outputs audio to speakers";
	}

	@Override
	public String[] getCategories() {
		return new String[]{"Routing"};
	}
}
