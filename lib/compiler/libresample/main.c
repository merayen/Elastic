#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "libresample.c"

int main() {
	int srclen = 50;
	int srcused = 0;

	double factor = 3;
	int dstlen = (int)(srclen * factor);

	float *src = (float *)malloc((srclen+100) * sizeof(float));
	float *dst = (float *)malloc((dstlen+100) * sizeof(float));

	for (int i = 0; i < srclen; i++) {
		src[i] = sin(i / (float)srclen * 8 * 2 * M_PI) + sin(i / (float)srclen * 16 * 2 * M_PI);
	}

	for (int i = 0; i < dstlen; i++) {
		dst[i] = -(float)i;
	}

  void* handle = resample_open(1, factor, factor);

	resample_process(
		handle, // void   *handle,
		factor, // double  factor,
		src, // float  *inBuffer,
		srclen, // int     inBufferLen,
		1, // int     lastFlag,
		&srcused, // int    *inBufferUsed,
		dst, // float  *outBuffer,
		dstlen // int     outBufferLen
	);

	for (int i = 0; i < (srclen>dstlen?srclen:dstlen); i++) {
		printf("%f %f\n", i<srclen?src[i]:0.0f, i<dstlen?dst[i]:0.0f);
	}
}
