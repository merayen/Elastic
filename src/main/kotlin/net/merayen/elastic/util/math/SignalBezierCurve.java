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
			float length = dots[i + 1].position.getX() - dots[i].position.getX();
			int length_samples = (int)(length * result.length);
			if(length_samples > 0)
				getValuesFromSegment2(dots[i], dots[i+1], result, (int)(dots[i].position.getX() * result.length), length_samples);
		}
	}

	public static void getValuesFromSegment(Dot dot0, Dot dot1, final float[] result, int start, int length) {
		int last = 0;
		float last_value = 0;

		for(float i = 0; i < length * 1.1; i++) {
			float v = i / (float)length;

			float x = getAxis(
				new float[]{
						dot0.position.getX(),
						dot0.right.getX(),
						dot1.left.getX(),
						dot1.position.getX()
				},
				v
			);

			float y = getAxis(
				new float[]{
						dot0.position.getY(),
						dot0.right.getY(),
						dot1.left.getY(),
						dot1.position.getY()
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
		float x_distance = (dot1.position.getX() - dot0.position.getX());

		float[] x_axis = new float[]{
			0,//dot0.position.x,
			(dot0.right.getX() - dot0.position.getX()) / x_distance,
			(dot1.left.getX() - dot0.position.getX()) / x_distance,
			1//dot1.position.x
		};

		float[] y_axis = new float[]{
				dot0.position.getY(),
				dot0.right.getY(),
				dot1.left.getY(),
				dot1.position.getY()
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
}
