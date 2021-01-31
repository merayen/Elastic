#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>

#define SIZE 104857600

float *data;
float *out;

void a(float *data, float *out) {
	for (long i = 0; i < SIZE; i++)
		out[i] = data[i];
}

void b(float *data, float *out) {
	for (long i = 0; i < SIZE; i++)
		out[i] /= 1000.0f;
}

void c(float *data, float *out) {
	for (long i = 0; i < SIZE; i++)
		out[i] = sin(out[i]);
}

void d(float *data, float *out) {
	for (long i = 0; i < SIZE; i++)
		out[i] *= 1.2f;
}

void e(float *data, float *out) {
	for (long i = 0; i < SIZE; i++)
		out[i] += 0.51f;
}

int main() {
	data = (float *)malloc(sizeof(float) * SIZE);
	out = (float *)malloc(sizeof(float) * SIZE);

	// This one is a bit slower than the loops below
	//for (long i = 0; i < SIZE; i++) 
	//	out[i] += sin(data[i] / 1000.0f) * 1.2f + 0.51f;

	// This one is 4-5% faster with GCC and clang-10!
	//for (long i = 0; i < SIZE; i++)
	//	out[i] = data[i];
	//for (long i = 0; i < SIZE; i++)
	//	out[i] /= 1000.0f;
	//for (long i = 0; i < SIZE; i++)
	//	out[i] = sin(out[i]);
	//for (long i = 0; i < SIZE; i++)
	//	out[i] *= 1.2f;
	//for (long i = 0; i < SIZE; i++)
	//	out[i] += 0.51f;

	a(data, out);
	b(data, out);
	c(data, out);
	d(data, out);
	e(data, out);

	// Loop unrolling, maybe done wrong. Identical performance as the one above
	//for (long i = 0; i < SIZE; i+=4) {
	//	data[i+0] += 1.2f;
	//	data[i+1] += 1.2f;
	//	data[i+2] += 1.2f;
	//	data[i+3] += 1.2f;
	//}
	//for (long i = 0; i < SIZE; i+=4) {
	//	data[i+0] = sin(data[i+0]);
	//	data[i+1] = sin(data[i+1]);
	//	data[i+2] = sin(data[i+2]);
	//	data[i+3] = sin(data[i+3]);
	//}
	//for (long i = 0; i < SIZE; i+=4) {
	//	data[i+0] *= 0.51f;
	//	data[i+1] *= 0.51f;
	//	data[i+2] *= 0.51f;
	//	data[i+3] *= 0.51f;
	//}

	return out[*((int *)(&out[1234])) % SIZE] > 4.0f;
}
