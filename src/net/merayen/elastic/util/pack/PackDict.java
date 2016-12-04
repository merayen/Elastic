package net.merayen.elastic.util.pack;

import java.util.HashMap;
import java.util.Map;

public class PackDict extends PackType {
	public Map<String, PackType> data = new HashMap<>();

	@Override
	public byte[] onDump() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte getIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}
}
