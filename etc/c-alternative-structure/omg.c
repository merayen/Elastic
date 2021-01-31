#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <stdbool.h>
#include <unistd.h>

pthread_mutex_t mutex;
pthread_mutex_t mutex_ready; // Mutex for tråden å rapportere at den er ferdig
pthread_cond_t cond;
pthread_cond_t cond_ready; // Tråd signaliserer med denne for å rapportere at den er ferdig
pthread_t thread;

void f(int ja) {
	if (ja != 0)
		exit(EXIT_FAILURE);
}

int thread_i = 0;

void *func(void *args) {
	f(pthread_mutex_lock(&mutex));

	while (true) {
		f(pthread_cond_wait(&cond, &mutex));
		//sleep(1);
		printf("Thread: %i\n", thread_i++);

		// Notify main thread we are done
		pthread_mutex_lock(&mutex_ready);
		pthread_cond_broadcast(&cond_ready);
		pthread_mutex_unlock(&mutex_ready);
	}
}

int main() {
	f(pthread_mutex_init(&mutex, NULL));
	f(pthread_mutex_init(&mutex_ready, NULL));
	f(pthread_cond_init(&cond, NULL));
	f(pthread_cond_init(&cond_ready, NULL));
	f(pthread_create(&thread, NULL, &func, NULL));

	sleep(0.5);

	f(pthread_mutex_lock(&mutex_ready)); // Låser alltid mutex_ready

	for (int i = 0; i < 10; i++) {
		f(pthread_mutex_lock(&mutex)); // Må låse, ellers er det udefinert hva som skjer (i følge dokumentasjon)
		f(pthread_cond_broadcast(&cond)); // Signaliser tråd at den har arbeid
		f(pthread_mutex_unlock(&mutex)); // Låser opp igjen for at tråd faktisk skal få jobbet

		f(pthread_cond_wait(&cond_ready, &mutex_ready));
		printf("Loop: %i\n", i);
	}
	f(pthread_mutex_unlock(&mutex_ready));

	return 0;
}

