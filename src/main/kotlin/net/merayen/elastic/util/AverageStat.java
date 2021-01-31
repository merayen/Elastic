package net.merayen.elastic.util;

import java.util.ArrayList;
import java.util.Iterator;

public class AverageStat<T extends Number> implements Iterable<T> {
	public static class Stat {
		public final double min, avg, max;

		private Stat() {
			min = avg = max = 0;
		}

		private Stat(double min, double avg, double max) {
			this.min = min;
			this.avg = avg;
			this.max = max;
		}
	}

	private ArrayList<T> array;
	private int pos = 0;
	private int length;

	public AverageStat(int length) {
		this.length = length;
		array = new ArrayList<>(length);
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
			if(x == null)
				break;

			r += x.doubleValue();
			i++;
		}
		if(i > 0)
			return r / i;
		else
			return 0;
	}

	public double getMax() {
		double max = Double.MIN_VALUE;
		for(Number x : array)
			if(x != null && x.doubleValue() > max)
				max = x.doubleValue();

		return max;
	}

	public double getMin() {
		double min = Double.MAX_VALUE;
		for(Number x : array)
			if(x != null && x.doubleValue() < min)
				min = x.doubleValue();

		return min;
	}

	public Stat get() {
		if(length == 0)
			return new Stat();

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for(T x : array) {
			if(x == null)
				break;

			double v = x.doubleValue();
			if(v > max)
				max = v;
			else if(v < min)
				min = v;
		}

		return new Stat(min != Double.MAX_VALUE ? min : 0, getAvg(), max != Double.MIN_VALUE ? max : 0);
	}

	public interface Formatter {
		String apply(double value);
	}

	public String info(Formatter formatter) {
		Stat stat = get();
		return String.format("[%s / %s / %s]", formatter.apply(stat.min), formatter.apply(stat.avg), formatter.apply(stat.max));
	}

	public String info() {
		return info(Double::toString);
	}

	@Override
	public Iterator<T> iterator() {
		return array.iterator();
	}
}
