#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>

struct {
	short noe0;
	short noe1;
	short noe2;
	short noe3;
	void *noe;
} noe;

int main() {
	printf("%li\n", sizeof(noe));
	return 0;
}

