package net.merayen.elastic.util.pack;

public class PackNumber extends PackType {
	public Number data;

	public PackNumber(Number number) {
		data = number;
	}

	public PackNumber() {
		data = 0;
	}

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
