import java.nio.FloatBuffer;

public class BufferTest {
	public static void main(String[] asdf) {
		FloatBuffer floatBuffer = FloatBuffer.allocate(10);
		floatBuffer.put(new float[]{0,1,2,3,4,5,6,7,8,9});
		floatBuffer.flip();
		floatBuffer.put(5);
	}
}
