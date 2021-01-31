#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>

struct Message {
	int length;
	short age;
	char *name;
};

int main() {
	char length_data[] = {123, 0, 0, 0};
	int length = *(int *)(&length_data);
	printf("%i\n", length);
	return 0;
}
