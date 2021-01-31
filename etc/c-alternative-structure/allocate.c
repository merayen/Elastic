#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>

int main() {
	const int COUNT = 1000000;
	void *omg[COUNT];
	for (int i = 0; i < COUNT; i++) {
		omg[i] = malloc(10);
		sleep(0.001);
	}

	printf("Hello\n");
	return 0;
}

