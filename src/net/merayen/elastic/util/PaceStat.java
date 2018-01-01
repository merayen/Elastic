package net.merayen.elastic.util;

public class PaceStat {
	private final long[] hits;
	private int position;

	public PaceStat(int size) {
		hits = new long[size];
	}

	public void hit() {
		hits[position++] = System.nanoTime();
	}

	public double getAvg() {
		double result = 0;

		for(int i = 1; i < hits.length; i++)
			result += hits[i] - hits[i - 1];

		return result / (double)hits.length;
	}

	public double getMax() {
		double result = 0;

		for(int i = 1; i < hits.length; i++) {
			double v = hits[i] - hits[i - 1];

		}
		return result;
	}
}
