package net.merayen.elastic.uinodes.list.poly_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override
	public String getName() {return "Poly";}

	@Override
	public String getDescription() {return "Polyphonic group";}

	@Override
	public String[] getCategories() {return new String[]{"Tool"};}
}
