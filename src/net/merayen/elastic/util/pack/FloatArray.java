package net.merayen.elastic.util.pack;

public class FloatArray extends PackType {
	public float[] data;

	public FloatArray() {}

	public FloatArray(float[] data) {
		this.data = data;
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
