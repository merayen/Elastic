package net.merayen.elastic.util.pack;

public class PackString extends PackType {
	public String data;

	public PackString(String str) {
		data = str;
	}

	@Override
	public byte[] onDump() {
		return new byte[0];
	}

	@Override
	public byte getIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}
}
