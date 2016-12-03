package net.merayen.elastic.util.pack;

public abstract class PackType {
	public byte[] dump() {
		byte[] data = onDump();
		int length = data.length;
		// TODO
		return null;
	}
	public abstract byte[] onDump();
	public abstract byte getIdentifier();
}
