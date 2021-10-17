#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>

struct b2f_Dot {
	float left_x;
	float left_y;
	float x;
	float y;
	float right_x;
	float right_y;
};

double bezier_to_float_get_axis(double p0, double p1, double p2, double p3, double t) {
	return (
		pow(1 - t, 3) * p0 +
		3 * pow(1 - t, 2) * t * p1 +
		3 * (1 - t) * pow(t, 2) * p2 +
		pow(t, 3) * p3
	);
}

void b2f_calc_segment(
	struct b2f_Dot dot0,
	struct b2f_Dot dot1,
	float* result,
	int result_length,
	char interpolate
) {
	double x_distance = dot1.x - dot0.x;

	double x_axis_p0 = 0;
	double x_axis_p1 = (dot0.right_x - dot0.x) / x_distance;
	double x_axis_p2 = (dot1.left_x - dot0.x) / x_distance;
	double x_axis_p3 = 1;

	double y_axis_p0 = dot0.y;
	double y_axis_p1 = dot0.right_y;
	double y_axis_p2 = dot1.left_y;
	double y_axis_p3 = dot1.y;

	int last = -1;
	double last_value = 0;

	double i = 0;
	int pos = 0;

	int expected_pos= 0;
	double speed = .5;
	int total_samples = 0;
	int total_iterations = 0;
	
	double threshold = 0.9;

	for (;;) {
		double v = i / (double)result_length;
		total_iterations++;

		double x = bezier_to_float_get_axis(
			x_axis_p0,
			x_axis_p1,
			x_axis_p2,
			x_axis_p3,
			v
		);

		if (x > 1) x = 1;
		if (x < 0) x = 0;

		pos = round(result_length * x);
		if (pos < 0) pos = 0;

		if (interpolate == 2) {
			double diff = (x * result_length) - expected_pos;
			//fprintf(stderr, "diff=%f, pos=%d, expected_pos=%d, speed=%f: ", diff, pos, expected_pos, speed);
			if (diff > threshold / result_length) {
				// Going too fast, beyond the threshold. Step back, slow down speed by difference and try again
				speed /= 2;
				i -= speed;
				//fprintf(stderr, "Step back\n");
				continue;
			} else if (diff < -threshold / result_length) {
				// Too small jump, don't sample
				expected_pos--;
				speed *= 1.9;
				//fprintf(stderr, "Increase speed");
			}
			//fprintf(stderr, "\n");
		}

		double y = bezier_to_float_get_axis(
			y_axis_p0,
			y_axis_p1,
			y_axis_p2,
			y_axis_p3,
			v
		);

		// Linear interpolation on missing samples in between. (this does perhaps not sound too good?) Not tested
		// TODO rather step half a step back and do the above calculations again?
		if (interpolate == 1) {
			for(int j = last + 1; j < pos; j++) {
				double k = (j - last) / (double)(pos - last);
				if (j < result_length)
					result[j] = (double)((1 - k) * last_value + k * y);
			}
		} 

		if (pos >= result_length) {
			result[result_length-1] = y;
			break;
		}

		if (pos > result_length -1) pos = result_length - 1;
		result[pos] = y;

		last = pos;
		last_value = y;
		expected_pos++;
		i += speed;
		total_samples++;
	}

	//fprintf(stderr, "total_samples=%i, total_iterations=%i\n", total_samples, total_iterations);
}

void b2f_calc_curve(
	struct b2f_Dot* dots,
	int dots_length,
	float *result,
	int result_length,
	int interpolate
) {
	// Sanity check
	for (int i = 0; i < dots_length; i++) {
		if (
			dots[i].left_x < 0 ||
			dots[i].left_x > 1 ||
			dots[i].x < 0 ||
			dots[i].x > 1 ||
			dots[i].right_x < 0 ||
			dots[i].right_x > 1
		) {
			fprintf(stderr, "b2f invalid dots\n");
			exit(EXIT_FAILURE);
		}
	}

	struct b2f_Dot dot0;
	struct b2f_Dot dot1;
	for (int i = 0; i < dots_length - 1; i++) {
		dot0 = dots[i];
		dot1 = dots[i+1];

		// Normalize coordinates
		double offset_x = dot0.x;
		double width = dot1.x - offset_x;

		if (width <= 0)
			continue; // Should we have some threshold...?

		dot0.x = 0;
		dot1.x = 1;
		dot0.right_x -= offset_x;
		dot1.left_x -= offset_x;
		dot0.right_x /= width;
		dot1.left_x /= width;

		float* output = result + (int)(offset_x * result_length);

		// Calculate output length to make sure to fill end up buffer (maybe we need it between segments too...)
		int output_length = (i==dots_length-2) ? (result_length - (output - result)) : (width * result_length);

		//fprintf(stderr, "%f, %f, %f, %ld\n", offset_x, width, offset_x*result_length, (void*)output-(void*)result);
		//fflush(stderr);


		b2f_calc_segment(
			dot0,
			dot1,
			output,
			output_length,
			2
		);
	}
}
