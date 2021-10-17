#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include "b2f.c"

int main() {
	int length = 256;
	float* result = calloc(length, sizeof(float));
	for (int i = 0; i < 1000; i++) { // Benchmark
		struct b2f_Dot dots[3];
		struct b2f_Dot* dot0 = &dots[0];
		struct b2f_Dot* dot1 = &dots[1];
		struct b2f_Dot* dot2 = &dots[2];

		dot0->left_x = 0;
		dot0->left_y = 0;
		dot0->x = 0;
		dot0->y = 0;
		dot0->right_x = .3;
		dot0->right_y = 1;

		dot1->left_x = 0;
		dot1->left_y = 1;
		dot1->x = 0.3;
		dot1->y = 0;
		dot1->right_x = 0.8;
		dot1->right_y = 1;

		dot2->left_x = 1;
		dot2->left_y = 0;
		dot2->x = 1;
		dot2->y = 1;
		dot2->right_x = 0;
		dot2->right_y = 0;

		for (int i = 0; i < length; i++) {
			result[i] = -1;
		}

		b2f_calc_curve(
			dots,
			3,
			result,
			length,
			0 // 0 = No interpolation, 1 = quick and dirty, 2 = slow but nicer interpolation
		);
	}

	printf("NaN\n");
	for (int i = 0; i < length; i++) {
		printf("%f\n", result[i]);
	}
}
