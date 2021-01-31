#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>

pthread_mutex_t mutex;
pthread_cond_t cond;

pthread_mutex_t mutex_ready;
pthread_cond_t cond_ready;

volatile bool shit_is_real = true;

volatile bool has_work = false;

int holy_counter = 0;
void *func(void *args) {
	printf("[thread] pthread_mutex_lock\n");
	pthread_mutex_lock(&mutex);
	while (shit_is_real) {
		printf("[thread] pthread_cond_wait\n");
		if (pthread_cond_wait(&cond, &mutex) != 0) // Wait for work
			exit(EXIT_FAILURE);

		if (!shit_is_real)
			break;

		if (!has_work)
			continue;

		has_work = false;

		printf("Counting\n");


		holy_counter++;

		printf("[thread] pthread_cond_broadcast\n");

		pthread_mutex_lock(&mutex_ready);
		if (pthread_cond_broadcast(&cond_ready) != 0)
			exit(EXIT_FAILURE);
		pthread_mutex_unlock(&mutex_ready);
	}

	pthread_mutex_unlock(&mutex);

	return NULL;
}

int main() {
	pthread_cond_init(&cond, NULL);
	pthread_cond_init(&cond_ready, NULL);
	pthread_mutex_init(&mutex, NULL);
	pthread_mutex_init(&mutex_ready, NULL);

	pthread_t thread;
	pthread_create(&thread, NULL, func, NULL);
	sleep(1);
	printf("Ready\n");

	for (int i = 0; i < 5; i++) {
		pthread_mutex_lock(&mutex);
		has_work = true;

		printf("[%i] pthread_cond_broadcast cond\n", i);
		if (pthread_cond_broadcast(&cond) != 0)
			exit(EXIT_FAILURE);

		pthread_mutex_unlock(&mutex);

		printf("[%i] pthread_cond_wait cond_ready\n", i);
		pthread_mutex_lock(&mutex_ready);
		if (pthread_cond_wait(&cond_ready, &mutex) != 0)
			exit(EXIT_FAILURE);
		pthread_mutex_unlock(&mutex_ready);
	}

	shit_is_real = false;
	printf("Going down... pthread_cond_broadcast\n");
	pthread_cond_broadcast(&cond);
	printf("Going down... pthread_join\n");
	pthread_join(thread, NULL);

	printf("Hello\n");
	return 0;
}

