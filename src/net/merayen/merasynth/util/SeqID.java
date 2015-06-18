package net.merayen.merasynth.util;

public class SeqID {

	private int id;
	
	public int next() {
		return ++id;
	}
	
	public void touch(int id) {
		this.id = Math.max(this.id, id);
	}
}
