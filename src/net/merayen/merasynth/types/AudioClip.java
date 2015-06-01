package net.merayen.merasynth.types;

public class AudioClip {
	/*
	 * 
	 */
	private float[] data;
	private int channels;
	
	public AudioClip(int channels, float[] data) {
		this.data = data;
		this.channels = channels;
	}
}
