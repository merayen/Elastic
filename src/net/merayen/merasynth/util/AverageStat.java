package net.merayen.merasynth.util;

import java.util.ArrayList;

public class AverageStat<T extends Number> {
	private ArrayList<T> array;
	private int pos = 0;
	private int length;

	public AverageStat(int length) {
		this.length = length;
		array = new ArrayList<T>(length);
		for(int i = 0; i < length; i++)
			array.add(null);
	}

	public void add(T v) {
		array.set(pos, v);
		pos++;
		pos %= length;
	}

	public double getAvg() {
		double r = 0;
		int i = 0;
		for(T x : array) {
			if(x != null) {
				r += x.doubleValue();
				i++;
			}
		}
		if(i > 0)
			return r / i;
		else
			return 0;
	}
}
