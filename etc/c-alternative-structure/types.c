#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>

int main() {
	int hei = 2147483647;
	unsigned char noe = *((char *)&hei);
	printf("%i\n", noe);
	return 0;
}

