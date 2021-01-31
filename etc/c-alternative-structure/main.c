/*
* Prototype of code gen from synth, how it should work and how to lay it out.
*/

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <string.h>
//#include <time.h>
#include <pthread.h>

#define VOICE_COUNT 2
#define THREAD_COUNT 4
#define FRAME_SAMPLE_SIZE 256
#define SAMPLE_RATE 44100

struct threads_t {
	pthread_t thread;
	pthread_cond_t cond;
	pthread_cond_t cond_started;
	pthread_mutex_t lock; // Only the one holding this lock should write to this struct
	volatile void *func; // Method to run
	volatile bool inited;
} threads[THREAD_COUNT];

volatile int session_id_counter = 0;
pthread_mutex_t session_id_counter_mutex;

// Port data formats
struct PortData_Audio {
	float audio[FRAME_SAMPLE_SIZE];
};

struct PortData_Midi {
	int length;

	struct {
		int length;
		unsigned char packet;
	} packets[];
};

// Node datas
struct Node_poly1 { // poly-node
	int sessions[VOICE_COUNT]; // == 0: voice not in use, != 0: voice in use, the session ID

	struct {
		struct {} parameter;

		struct {
			struct PortData_Midi out;
		} *ports[VOICE_COUNT];

	} node_in1;

	struct {
		struct {
			float frequency;
		} parameters;

		struct {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_sine1;

	struct {
		struct {
			float frequency;
		} parameters;

		struct {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_square1;

	struct {
		struct {
			float mix[VOICE_COUNT];
		} parameters;

		struct {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_mix1;

	struct { } node_out1;

} node_poly1;

struct Node_poly2 {
	int sessions[VOICE_COUNT];

	struct {
		struct {
			float frequency;
		} parameters;

		struct {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_sine2;

	struct {
		struct {

		} parameters;

		struct {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_square2;

} node_poly2;

struct Node_group1 {
	int sessions[VOICE_COUNT]; // == 0: voice not in use, != 0: voice in use

	struct {
		struct Node_randommidi1_port {
			struct PortData_Midi out;
		} *ports[VOICE_COUNT];
	} node_randommidi1;

	struct {
		struct {
			int voices;
		} parameters;

		struct Node_poly1_port {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];

	} node_poly1;

	struct {
		struct {
			float frequency;
		} parameters;

		struct Node_poly2_port {
			struct PortData_Audio out;
		} *ports[VOICE_COUNT];
	} node_poly2;

	struct {
		struct Node_speaker1_port {
		} *ports[VOICE_COUNT];

		struct {
			float volume;
		} parameters;
	} node_speaker1;

} node_group1;


// Utility methods
pthread_t *run_in_thread(void *func, int session_id) { // Will run a function with the argument "session". Will poll several threads or just block if no threads available
	while (true) { // We retry forever until we found a thread that has no work as no heavy-work is to be done in main-thread
		for (int i = 0; i < THREAD_COUNT; i++) {
			if (pthread_mutex_trylock(&threads[i].lock) == 0) {
				// Found a thread that is available
				threads[i].func = func;
				pthread_cond_broadcast(&threads[i].cond);
				pthread_mutex_unlock(&threads[i].lock);
				return &threads[i].thread;
			}
		}
		sleep(0.01);
	}
}


// Group-node processors
void process_poly2(int session) {
	
}

void process_poly1(int session) {

}

void process_group1(int session) {
	if (session == 0) {
		fprintf(stderr, "Session can not be 0!\n");
		exit(EXIT_FAILURE);
	}

	int sessions_index = -1;
	for (int i = 0; i < VOICE_COUNT; i++) {
		int x = node_group1.sessions[i];
		if (x == session) {
			sessions_index = i;
			break;
		}
	}

	// node_randommidi1
	for (int i_session = 0; i_session < VOICE_COUNT; i_session++) {
		if (node_group1.sessions[i_session] != session)
			continue;
	}
}

int create_voice_group1() { // Creates a new voice for the group1-node. Returns 0 if no more voices left
	pthread_mutex_lock(&session_id_counter_mutex);
	int session_id = ++session_id_counter;
	pthread_mutex_unlock(&session_id_counter_mutex);
	
	bool success = false;
	for (int i_session = 0; i_session < VOICE_COUNT; i_session++) {
		int session = node_group1.sessions[i_session];
		if (session != 0) continue;

		if (node_group1.node_randommidi1.ports[i_session] != 0) {
			fprintf(stderr, "Port has not been cleaned up correctly\n");
			exit(EXIT_FAILURE);
		}
		
		node_group1.sessions[i_session] = session_id;

		// Initialize all ports for session
		node_group1.node_randommidi1.ports[i_session] = (struct Node_randommidi1_port *)calloc(1, sizeof(struct Node_randommidi1_port));
		node_group1.node_poly1.ports[i_session] = (struct Node_poly1_port *)calloc(1, sizeof(struct Node_poly1_port));
		node_group1.node_poly2.ports[i_session] = (struct Node_poly2_port *)calloc(1, sizeof(struct Node_poly2_port));
		node_group1.node_speaker1.ports[i_session] = (struct Node_speaker1_port *)calloc(1, sizeof(struct Node_speaker1_port));

		success = true;

		break;
	}

	if (success)
		return session_id;
	else
		return 0;
}

void destroy_voice_group1() {

}


// Control methods
void *thread_runner(void *args) { // Runs threads
	int thread_index = *((int *)args);

	if (thread_index < 0 || thread_index >= THREAD_COUNT)
		exit(EXIT_FAILURE);

	struct threads_t *thread = &threads[thread_index];

	printf("[%i] Launched\n", thread_index);
	pthread_mutex_lock(&thread->lock); // Lock to us all the time, as long as we do not pthread_cond_wait

	while (true) {
		thread->inited = true;

		pthread_cond_broadcast(&thread->cond);
		if (thread->func == NULL) { // No job set, go back to sleep
			fprintf(stderr, "[%i] No job set, waiting for work\n", thread_index);
			pthread_cond_wait(&thread->cond, &thread->lock); // Wait for someone to wake us up, also temporary unlocks the mutex
			continue; // Woken up, look for job again
		}

		printf("Thread %i has gotten work\n", thread_index);
		// TODO call func here
		thread->func = NULL;
	}
}

void init_threads() {
	pthread_mutex_init(&session_id_counter_mutex, NULL);

	// Clear memory
	memset(threads, 0, sizeof(threads));

	// Set up conditions, that we use to make the threads sleep
	for (int i = 0; i < THREAD_COUNT; i++) {
		pthread_cond_init(&threads[i].cond_started, NULL);
		pthread_cond_init(&threads[i].cond, NULL);
	}

	// Init mutexes
	for (int i = 0; i < THREAD_COUNT; i++) {
		pthread_mutex_init(&threads[i].lock, NULL);
	}

	// Create and run the threads
	for (int i = 0; i < THREAD_COUNT; i++) {
		int *thread_index = malloc(sizeof(int));
		*thread_index = i;

		printf("[main:%i] Locking\n", i);
		pthread_mutex_lock(&threads[i].lock); // Lock thread before running it, 

		pthread_create(&threads[i].thread, NULL, thread_runner, thread_index);
	}

	// Wait for all threads to have inited
	for (int i = 0; i < THREAD_COUNT; i++) {
		printf("[main:%i] Waiting for thread to start\n", i);
		while (true) {
			pthread_cond_wait(&threads[i].cond, &threads[i].lock);

			printf("[main:%i] Woke up\n", i);

			if (threads[i].inited)
				break;

			printf("[main:%i] Not ready yet, trying again\n", i);
		}
		printf("[main:%i] Thread reported to have started, unlocking it from main thread\n", i);
		pthread_mutex_unlock(&threads[i].lock); // Unlock the thread from us
	}
}

int init_stdinout() { // Initializes communication using stdin and stdout
	FILE *result = freopen(NULL, "rb", stdin);
	if (result == NULL)
		return 1;

	result = freopen(NULL, "wb", stdout);
	if (result == NULL)
		return 1;

	char hello[] = {'H','E','L','L','O'};
	fwrite(hello, 1, sizeof hello, stdout);

	fflush(stdout); // Not needed?

	char buf[4 + 3];

	fread(buf, 1, 4 + 3, stdin);

	char* ut = malloc(4 + 4);

	(*ut) = (int)4; // Size of return data

	if (buf[4] == 10 && buf[5] == 11 && buf[6] == 12) {
		ut[4] = 'G';
		ut[5] = 'O';
		ut[6] = 'O';
		ut[7] = 'D';
	} else {
		ut[4] = 'F';
		ut[5] = 'A';
		ut[6] = 'I';
		ut[7] = 'L';
	}

	fwrite(ut, 1, 8, stdout);
	printf("\n");

	free(ut);

	return 0;
}

void init_topnode() { // Initializes the top-most node as that one always have exact 1 voice
	if (create_voice_group1() != 1) {
		fprintf(stderr, "Could not create voice on topmost node\n");
		exit(EXIT_FAILURE);
	}
}

void wait_for_data() {
	// Fakes data as this is a prototype
}

void process() { // Will launch and run stuff in the threads set and then wait for all of them to be finished
	printf("=== Processing a frame ===\n");
	process_group1(1); // Runs topmost node in main-thread, always ID 1 session as it is the main-session
}

void send_back() { // Send data back to Java-app

}

int main() {
	init_threads();
	init_stdinout();
	init_topnode();
	for (int i = 0; i < 10; i++) {
		wait_for_data();
		process();
		send_back();
	}

	printf("Memsize: %li bytes\n", (sizeof(node_group1) + sizeof(node_poly1) + sizeof(node_poly2)));
	return 0;
}

