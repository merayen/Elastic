package net.merayen.elastic.uinodes.list.midi_spread_1;

import net.merayen.elastic.uinodes.BaseInfo;

public class Info implements BaseInfo {
	@Override public String getName() {return "Midi Spread";}
	@Override public String getDescription() {return "Spreads midi notes, making sound fuller";}
	@Override public String[] getCategories() {return new String[]{"Tools"};}
}
