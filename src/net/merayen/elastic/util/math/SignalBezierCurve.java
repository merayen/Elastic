package net.merayen.elastic.util.math;

import net.merayen.elastic.util.math.BezierCurve.Dot;

/**
 * Bezier curve tools for signals.
 */
public class SignalBezierCurve {
	private SignalBezierCurve() {} // Just a utility class for now

	/**
	 * Calculates the values 
	 * @return
	 */
	/*public static float[] getValues(Point[] points) {
		
	}*/

	private static void no() {
		throw new RuntimeException("Nope");
	}

	/**
	 * Calculate values into a float-array.
	 * @param result Results are stored in here. Its length decides how many samples that are to be done
	 */
	public static void getValues(Dot[] dots, final float[] result) {
		if(dots.length < 2)
			throw new RuntimeException("There has to be minimum 2 dots");

		for(int i = 0; i < dots.length - 1; i++) {
			float length = dots[i+1].position.x - dots[i].position.x;
			int length_samples = (int)(length * result.length);
			if(length_samples > 0)
				getValuesFromSegment2(dots[i], dots[i+1], result, (int)(dots[i].position.x * result.length), length_samples);
		}
	}

	private static void getValuesFromSegment(Dot dot0, Dot dot1, final float[] result, int start, int length) {
		int last = 0;
		float last_value = 0;

		for(float i = 0; i < length * 1.1; i++) {
			float v = i / (float)length;

			float x = getAxis(
				new float[]{
					dot0.position.x,
					dot0.right.x,
					dot1.left.x,
					dot1.position.x
				},
				v
			);

			float y = getAxis(
				new float[]{
					dot0.position.y,
					dot0.right.y,
					dot1.left.y,
					dot1.position.y
				},
				v
			);

			int pos = Math.max(0, Math.min(start + length - 1, Math.round(result.length * x)));
			result[pos] = y;

			if(i == 0) {
				last = pos;
				last_value = y;
			}

			// Linear interpolation on missing samples in between. (this does perhaps not sound too good?) Not tested
			// TODO rather step half a step back and do the above calculations again?
			for(int j = last + 1; j < pos; j++) {
				double k = (j - last) / (double)(pos - last);
				result[j] = (float)((1 - k) * last_value + k * y);
				//System.out.println("Interpolated " + j + " " + result[j]);
			}

			last = pos;
			last_value = y;
		}

		if(last < start + length - 1)
			System.out.println("Nope");

		//System.out.println(start + "\t" + pos + "\t" + length + "\t" + x);
	}

	private static void getValuesFromSegment2(Dot dot0, Dot dot1, final float[] result, int start, int length) {
		int last = 0;
		float last_value = 0;

		// Normalizing X-axis
		float x_distance = (dot1.position.x - dot0.position.x);

		float[] x_axis = new float[]{
			0,//dot0.position.x,
			(dot0.right.x - dot0.position.x) / x_distance,
			(dot1.left.x - dot0.position.x) / x_distance,
			1//dot1.position.x
		};

		float[] y_axis = new float[]{
			dot0.position.y,
			dot0.right.y,
			dot1.left.y,
			dot1.position.y
		};

		int i = 0, pos = 0, pos_index = 0, protection = 0;
		float x = 0,y = 0;
		while(protection++ < 10000) {
			float v = i / (float)length;

			x = Math.min(1, Math.max(0, getAxis(
				x_axis,
				v
			)));

			y = getAxis(
				y_axis,
				v
			);

			pos = Math.round(start + (length) * x);
			pos_index = Math.max(0, Math.min(result.length - 1, pos));
			result[pos_index] = y;

			if(y > 1 || y < 0)
				System.console();

			if(i == 0) {
				last = pos;
				last_value = y;
			}

			// Linear interpolation on missing samples in between. (this does perhaps not sound too good?) Not tested
			// TODO rather step half a step back and do the above calculations again?
			for(int j = last + 1; j < pos_index; j++) {
				double k = (j - last) / (double)(pos - last);
				result[j] = (float)((1 - k) * last_value + k * y);
				//System.out.println("Interpolated " + j + " " + result[j]);
			}

			if(x >= 1.0f)
				break;

			last = pos;
			last_value = y;
			i++;
		}

		//if(last < start + length - 1)
		//	System.out.println("Nope");

		//System.out.println(start + "\t" + pos + "\t" + pos_index + "\t" + start + "\t" + length + "\t" + x + "\t" + protection);
	}

	private static float getAxis(float[] p, float t) {
		double one = Math.pow(1 - t, 3) * p[0];
		double two = 3 * Math.pow(1 - t, 2) * t * p[1];
		double three = 3 * (1 - t) * Math.pow(t, 2) * p[2];
		double four = Math.pow(t, 3) * p[3];

		return (float)(one + two + three + four);
	}

	public static void test() {
		Dot dot0 = new Dot();
		Dot dot1 = new Dot();

		dot0.position.x = 0;
		dot0.position.y = 0.5f;
		dot0.right.x = 0.5f;
		dot0.right.y = 0;

		dot1.position.x = 1;
		dot1.position.y = 0.5f;
		dot1.left.x = 0.5f;
		dot1.left.y = 1;

		final int ITERATIONS = 100;
		float[] result = new float[ITERATIONS];

		getValuesFromSegment(dot0, dot1, result, 0, result.length);

		int i = 0;
		for(float r : result) {
			double fasit = Math.sin(-Math.PI * 2 * (i++ / (float)ITERATIONS)) / 6.95 + 0.5f;
			System.out.println(String.format("%.3f - %.3f (%.3f)\t-> %.3f%%", r, fasit, fasit - r, Math.abs(r - 0.5) / Math.abs(fasit - 0.5) * 100));
			if(Math.abs(fasit - r) > 0.01)
				no();
		}
	}
}
