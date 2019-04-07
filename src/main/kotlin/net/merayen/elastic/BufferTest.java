package net.merayen.elastic;

import java.nio.FloatBuffer;

public class BufferTest {
	public static void main(String[] asdf) {
		FloatBuffer floatBuffer = FloatBuffer.allocate(10);
		System.out.println(floatBuffer.get());
		floatBuffer.limit(5);
		System.out.println(floatBuffer.remaining());
		System.out.println(floatBuffer.capacity());
		System.out.println(floatBuffer.position());
		System.out.println(floatBuffer.limit());
	}
}
