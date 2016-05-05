package net.merayen.elastic.backend.architectures.local;

/**
 * Every output port has a PortResult-buffer, and only one for each processor.
 * This buffer is reused.
 */
public class PortResult {
	public final float[] samples;

	/**
	 * Length that is available in this buffer.
	 * This will only be different from the size *IF* the sending processor
	 * has not been able to process the requested size due to tight feedback loops.
	 * The readers of this buffers must keep track of their positions in this buffer
	 * themselves.
	 * Data after the length can be random or old data and should not be read.
	 * 
	 * This is done to keep object creation and garbage to a minimum.
	 */
	public int length; 

	public PortResult(int size) {
		samples = new float[size];
	}
}
