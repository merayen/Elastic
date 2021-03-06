// This is the original generated source
/*
 * Code generated by Elastic DAW.
 * http://www.merayen.net/elastic
 */
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <string.h>
#include <time.h>
#include <pthread.h>
#include <unistd.h>
#include <stdarg.h>
#include <errno.h>
void exit_failure();
pthread_mutex_t fprintf_mutex;
struct threads_t {
	pthread_t thread;
	pthread_cond_t cond;
	pthread_cond_t cond_started;
	pthread_mutex_t lock;
	volatile void* func;
	volatile bool inited;
}threads[4];
void handle_ingoing_nodedata();
void send(int length, void* data) {
	fwrite(&length, 1, 4, stdout);
	if (length > 0) {
		fwrite(data, 1, length, stdout);
	}
	fflush(stdout);
}
void send_text(char* text) {
	int length = strlen(text);
	fwrite(&length, 1, 4, stdout);
	fwrite(text, 1, length, stdout);
	fflush(stdout);
}
bool consume_text(char* what, char* data, int *offset, int length) {
	bool matches = false;
	for (int i = *offset, j = 0; i < length; i++, j++) {
		if (what[j] == '\x00') {
			*offset = i;
			return true;
		}
		else if (what[j] != data[i]) {
			return false;
		}
		matches = true;
	}
	return matches;
}
void process_communication() {
	handle_ingoing_nodedata();
}
pthread_t* queue_task(void *func) {
	while (true) {
		for (int i = 0; i < 4; i++) {
			bool lock_success = false;
			int _trylock_result;
			_trylock_result = pthread_mutex_trylock(&threads[i].lock);
			if (_trylock_result == 0) {
				if (threads[i].func == NULL) {
					lock_success = true;
					threads[i].func = func;
					int _broadcast_result;
					_broadcast_result = pthread_cond_broadcast(&threads[i].cond);
					if (_broadcast_result != 0) {
						exit(150);
					}
				}
				pthread_mutex_unlock(&threads[i].lock);
			}
			else if (_trylock_result == EINVAL) {
				exit(150);
			}
			else if (_trylock_result == EAGAIN) {
				exit(150);
			}
			if (lock_success) {
				return &threads[i].thread;
			}
		}
	}
	usleep(1000);
}
void* thread_runner(void *args) {
	int thread_index = *((int *)args);
	if (thread_index < 0 || thread_index >= 4) {
		exit(EXIT_FAILURE);
	}
	struct threads_t* thread = &threads[thread_index];
	pthread_mutex_lock(&thread->lock);
	while (true) {
		thread->inited = true;
		pthread_cond_broadcast(&thread->cond);
		if (thread->func == NULL) {
			pthread_cond_wait(&thread->cond, &thread->lock);
			continue;
		}
		((void(*)())thread->func)();
		thread->func = NULL;
	}
}
void init_threads() {
	memset(threads, 0, sizeof(threads));
	for (int i = 0; i < 4; i++) {
		pthread_cond_init(&threads[i].cond_started, NULL);
		pthread_cond_init(&threads[i].cond, NULL);
	}
	for (int i = 0; i < 4; i++) {
		pthread_mutex_init(&threads[i].lock, NULL);
	}
	for (int i = 0; i < 4; i++) {
		int *thread_index = malloc(sizeof(int));
		*thread_index = i;
		pthread_mutex_lock(&threads[i].lock);
		pthread_create(&threads[i].thread, NULL, thread_runner, thread_index);
	}
	for (int i = 0; i < 4; i++) {
		while (true) {
			pthread_cond_wait(&threads[i].cond, &threads[i].lock);
			if (threads[i].inited) {
				break;
			}
		}
		pthread_mutex_unlock(&threads[i].lock);
	}
}
struct PortDataAudio {
	int channels;
	float* audio;
};
struct PortDataAudio* PortDataAudio_create() {
	struct PortDataAudio* this = calloc(1, sizeof(struct PortDataAudio));
	if (this == NULL) {
		exit(150);
	}
	 this->audio = malloc(256 * 2 * sizeof(float));
	if (this->audio == NULL) {
		exit(150);
	}
	return this;
}
void PortDataAudio_init(struct PortDataAudio* this) {
	 this->audio = malloc(256 * 2 * sizeof(float));
	if (this->audio == NULL) {
		exit(150);
	}
}
void PortDataAudio_destroy(struct PortDataAudio* this) {
	if (this->audio != NULL) {
		free(this->audio);
	}
	free(this);
}
void PortDataAudio_prepare(struct PortDataAudio* this, int channels) {
	if (this->channels != channels) {
		if (this->audio != NULL) {
			free(this->audio);
		}
		 this->audio = malloc(256 * channels * sizeof(float));
		if (this->audio == NULL) {
			exit(150);
		}
	}
}
struct PortDataSignal {
	float signal[256];
};
struct PortDataSignal* PortDataSignal_create() {
	struct PortDataSignal* this = calloc(1, sizeof(struct PortDataSignal));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void PortDataSignal_init(struct PortDataSignal* this) {
}
void PortDataSignal_destroy(struct PortDataSignal* this) {
	free(this);
}
struct PortDataMidi {
	int capacity;
	int count;
	unsigned char* messages;
};
struct PortDataMidi* PortDataMidi_create() {
	struct PortDataMidi* this = calloc(1, sizeof(struct PortDataMidi));
	if (this == NULL) {
		exit(150);
	}
	this->capacity = 0;
	this->count = 0;
	this->messages = NULL;
	return this;
}
void PortDataMidi_init(struct PortDataMidi* this) {
	this->capacity = 0;
	this->count = 0;
	this->messages = NULL;
}
void PortDataMidi_destroy(struct PortDataMidi* this) {
	if (this->messages != NULL) {
		free(this->messages);
	}
	free(this);
}
void PortDataMidi_prepare(struct PortDataMidi* this, int count) {
	if (this->count < count) {
		if (this->messages != NULL) {
			free(this->messages);
		}
		 this->messages = calloc(3 * count, sizeof(unsigned char));
		if (this->messages == NULL) {
			exit(150);
		}
	}
	this->count = count;
}
struct Node_add {
	struct NodePorts_add {
		struct PortDataSignal out;
	}*outlets[256];
	struct  {
	}parameters;
}*nodedata_add;
struct Node_top {
	struct NodePorts_top {
	}*outlets[256];
	struct  {
	}parameters;
	char voices[256];
}*nodedata_top;
struct Node_value2 {
	struct NodePorts_value2 {
		struct PortDataSignal out;
	}*outlets[256];
	struct  {
		float value;
	}parameters;
}*nodedata_value2;
struct Node_value1 {
	struct NodePorts_value1 {
		struct PortDataSignal out;
	}*outlets[256];
	struct  {
		float value;
	}parameters;
}*nodedata_value1;
struct Node_out {
	struct NodePorts_out {
	}*outlets[256];
	struct  {
	}parameters;
}*nodedata_out;
void Node_add_prepare(struct Node_add* this);
void Node_add_create_voice(struct Node_add* this, int voice_index);
void Node_top_prepare(struct Node_top* this);
void Node_top_create_voice(struct Node_top* this, int voice_index);
void Node_value2_prepare(struct Node_value2* this);
void Node_value2_create_voice(struct Node_value2* this, int voice_index);
void Node_value1_prepare(struct Node_value1* this);
void Node_value1_create_voice(struct Node_value1* this, int voice_index);
void Node_out_prepare(struct Node_out* this);
void Node_out_create_voice(struct Node_out* this, int voice_index);
struct Node_add* Node_add_create() {
	struct Node_add* this = calloc(1, sizeof(struct Node_add));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void Node_add_init(struct Node_add* this) {
}
void Node_add_destroy(struct Node_add* this) {
	free(this);
}
void Node_add_prepare(struct Node_add* this) {
}
void Node_add_process(struct Node_add* this) {
	for (int voice_index = 0; voice_index < 256; voice_index++) {
		if (nodedata_top->voices[voice_index] == 0) {
			continue;
		}
		for (int i = 0; i < 256; i++) {
			nodedata_add->outlets[voice_index]->out.signal[i] = nodedata_value1->outlets[voice_index]->out.signal[i] + nodedata_value2->outlets[voice_index]->out.signal[i];
		}
	}
}
void Node_add_receive_data(struct Node_add* this) {
}
void Node_add_create_voice(struct Node_add* this, int voice_index) {
	 nodedata_add->outlets[voice_index] = calloc(1, sizeof(struct NodePorts_add));
	if (nodedata_add->outlets[voice_index] == NULL) {
		exit(150);
	}
	PortDataSignal_init(&nodedata_add->outlets[voice_index]->out);
}
void Node_add_destroy_voice(struct Node_add* this, int voice_index) {
}
struct Node_top* Node_top_create() {
	struct Node_top* this = calloc(1, sizeof(struct Node_top));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void Node_top_init(struct Node_top* this) {
}
void Node_top_destroy(struct Node_top* this) {
	free(this);
}
void Node_top_prepare(struct Node_top* this) {
}
void Node_top_process(struct Node_top* this) {
}
void Node_top_receive_data(struct Node_top* this) {
}
void Node_top_create_voice(struct Node_top* this, int voice_index) {
}
void Node_top_destroy_voice(struct Node_top* this, int voice_index) {
}
struct Node_value2* Node_value2_create() {
	struct Node_value2* this = calloc(1, sizeof(struct Node_value2));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void Node_value2_init(struct Node_value2* this) {
}
void Node_value2_destroy(struct Node_value2* this) {
	free(this);
}
void Node_value2_prepare(struct Node_value2* this) {
}
void Node_value2_process(struct Node_value2* this) {
	for (int voice_index = 0; voice_index < 256; voice_index++) {
		if (nodedata_top->voices[voice_index] == 0) {
			continue;
		}
		for (int i = 0; i < 256; i++) {
			nodedata_value2->outlets[voice_index]->out.signal[i] = nodedata_value2->parameters.value;
		}
	}
}
void Node_value2_receive_data(struct Node_value2* this) {
	nodedata_value2->parameters.value = 2;
}
void Node_value2_create_voice(struct Node_value2* this, int voice_index) {
	 nodedata_value2->outlets[voice_index] = calloc(1, sizeof(struct NodePorts_value2));
	if (nodedata_value2->outlets[voice_index] == NULL) {
		exit(150);
	}
	PortDataSignal_init(&nodedata_value2->outlets[voice_index]->out);
}
void Node_value2_destroy_voice(struct Node_value2* this, int voice_index) {
}
struct Node_value1* Node_value1_create() {
	struct Node_value1* this = calloc(1, sizeof(struct Node_value1));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void Node_value1_init(struct Node_value1* this) {
}
void Node_value1_destroy(struct Node_value1* this) {
	free(this);
}
void Node_value1_prepare(struct Node_value1* this) {
}
void Node_value1_process(struct Node_value1* this) {
	for (int voice_index = 0; voice_index < 256; voice_index++) {
		if (nodedata_top->voices[voice_index] == 0) {
			continue;
		}
		for (int i = 0; i < 256; i++) {
			nodedata_value1->outlets[voice_index]->out.signal[i] = nodedata_value1->parameters.value;
		}
	}
}
void Node_value1_receive_data(struct Node_value1* this) {
	nodedata_value1->parameters.value = 1;
}
void Node_value1_create_voice(struct Node_value1* this, int voice_index) {
	 nodedata_value1->outlets[voice_index] = calloc(1, sizeof(struct NodePorts_value1));
	if (nodedata_value1->outlets[voice_index] == NULL) {
		exit(150);
	}
	PortDataSignal_init(&nodedata_value1->outlets[voice_index]->out);
}
void Node_value1_destroy_voice(struct Node_value1* this, int voice_index) {
}
struct Node_out* Node_out_create() {
	struct Node_out* this = calloc(1, sizeof(struct Node_out));
	if (this == NULL) {
		exit(150);
	}
	return this;
}
void Node_out_init(struct Node_out* this) {
}
void Node_out_destroy(struct Node_out* this) {
	free(this);
}
void Node_out_prepare(struct Node_out* this) {
}
void Node_out_process(struct Node_out* this) {
}
void Node_out_receive_data(struct Node_out* this) {
}
void Node_out_create_voice(struct Node_out* this, int voice_index) {
}
void Node_out_destroy_voice(struct Node_out* this, int voice_index) {
}
void handle_ingoing_nodedata() {
	Node_add_receive_data(nodedata_add);
	Node_top_receive_data(nodedata_top);
	Node_value2_receive_data(nodedata_value2);
	Node_value1_receive_data(nodedata_value1);
	Node_out_receive_data(nodedata_out);
}
void init_nodes() {
	nodedata_add = Node_add_create();
	nodedata_top = Node_top_create();
	nodedata_value2 = Node_value2_create();
	nodedata_value1 = Node_value1_create();
	nodedata_out = Node_out_create();
}
void destroy_nodes() {
	Node_add_destroy(nodedata_add);
	Node_top_destroy(nodedata_top);
	Node_value2_destroy(nodedata_value2);
	Node_value1_destroy(nodedata_value1);
	Node_out_destroy(nodedata_out);
}
void exit_failure() {
	destroy_nodes();
	exit(EXIT_FAILURE);
}
pthread_mutex_t work_units_mutex;
pthread_cond_t work_units_cond;
char work_units[5];
void prepare_workunits() {
	pthread_mutex_lock(&work_units_mutex);
	work_units[0] = 0;
	work_units[1] = 0;
	work_units[2] = 0;
	work_units[3] = 0;
	work_units[4] = 0;
	pthread_mutex_unlock(&work_units_mutex);
}
void process_workunit_0();
void process_workunit_1();
void process_workunit_2();
void process_workunit_3();
void process_workunit_4();
void process_workunit_0() {
	// [value2]
	Node_value2_process(nodedata_value2);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			work_units[0] = 2;
			int work_units_cond_broadcast_result;
			work_units_cond_broadcast_result = pthread_cond_broadcast(&work_units_cond);
			if (work_units_cond_broadcast_result != 0) {
				exit(150);
			}
			queue_task(process_workunit_1);
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void process_workunit_1() {
	// [add]
	pthread_mutex_lock(&work_units_mutex);
	if (work_units[1] != 0) {
		pthread_mutex_unlock(&work_units_mutex);
		return ;
	}
	bool all_finished = work_units[0] == 2 && work_units[3] == 2;
	if (all_finished) {
		work_units[1] = 1;
	}
	pthread_mutex_unlock(&work_units_mutex);
	if (!all_finished) {
		return ;
	}
	Node_add_process(nodedata_add);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			work_units[1] = 2;
			int work_units_cond_broadcast_result;
			work_units_cond_broadcast_result = pthread_cond_broadcast(&work_units_cond);
			if (work_units_cond_broadcast_result != 0) {
				exit(150);
			}
			queue_task(process_workunit_2);
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void process_workunit_2() {
	// [out]
	Node_out_process(nodedata_out);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			work_units[2] = 2;
			int work_units_cond_broadcast_result;
			work_units_cond_broadcast_result = pthread_cond_broadcast(&work_units_cond);
			if (work_units_cond_broadcast_result != 0) {
				exit(150);
			}
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void process_workunit_3() {
	// [value1]
	Node_value1_process(nodedata_value1);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			work_units[3] = 2;
			int work_units_cond_broadcast_result;
			work_units_cond_broadcast_result = pthread_cond_broadcast(&work_units_cond);
			if (work_units_cond_broadcast_result != 0) {
				exit(150);
			}
			queue_task(process_workunit_1);
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void process_workunit_4() {
	// [top]
	Node_top_process(nodedata_top);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			work_units[4] = 2;
			int work_units_cond_broadcast_result;
			work_units_cond_broadcast_result = pthread_cond_broadcast(&work_units_cond);
			if (work_units_cond_broadcast_result != 0) {
				exit(150);
			}
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void process_workunits() {
	prepare_workunits();
	queue_task(process_workunit_0);
	queue_task(process_workunit_3);
	queue_task(process_workunit_4);
	 {
		int work_units_mutex_lock_result;
		work_units_mutex_lock_result = pthread_mutex_lock(&work_units_mutex);
		if (work_units_mutex_lock_result == 0) {
			double start;
			 {
				struct timespec tid;
				clock_gettime(CLOCK_MONOTONIC_RAW, &tid);
				start = tid.tv_sec + tid.tv_nsec / 1E9;
			}
			for (int i = 0; i < 5; i++) {
				while (work_units[i] != 2) {
					int work_units_cond_result;
					 {
						struct timespec work_units_cond_wait_until;
						clock_gettime(CLOCK_REALTIME, &work_units_cond_wait_until);
						work_units_cond_wait_until.tv_sec += 0;
						work_units_cond_wait_until.tv_nsec += 100000000;
						work_units_cond_wait_until.tv_sec += work_units_cond_wait_until.tv_nsec / 1000000000;
						work_units_cond_wait_until.tv_nsec %= 1000000000;
						work_units_cond_result = pthread_cond_timedwait(&work_units_cond, &work_units_mutex, &work_units_cond_wait_until);
					}
					if (work_units_cond_result == EINVAL) {
						exit(150);
					}
					else if (work_units_cond_result == EPERM) {
						exit(150);
					}
					else if (work_units_cond_result == ETIMEDOUT) {
						exit(150);
					}
					double now;
					 {
						struct timespec tid;
						if (clock_gettime(CLOCK_MONOTONIC_RAW, &tid)) {
							exit_failure();
						}
						now = tid.tv_sec + tid.tv_nsec / 1E9;
					}
					if (now - start >= 1.0) {
						exit(EXIT_FAILURE);
					}
				}
			}
			pthread_mutex_unlock(&work_units_mutex);
		}
		else if (work_units_mutex_lock_result == EINVAL) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EAGAIN) {
			exit(150);
		}
		else if (work_units_mutex_lock_result == EDEADLK) {
			exit(150);
		}
		else {
			exit(150);
		}
	}
}
void init_workunits() {
	int work_units_mutex_init_result;
	work_units_mutex_init_result = pthread_mutex_init(&work_units_mutex, NULL);
	if (work_units_mutex_init_result == 0) {
	}
	else if (work_units_mutex_init_result == EAGAIN) {
		exit(150);
	}
	else if (work_units_mutex_init_result == ENOMEM) {
		exit(150);
	}
	else if (work_units_mutex_init_result == EPERM) {
		exit(150);
	}
	else {
		exit(150);
	}
	int work_units_cond_init_result;
	work_units_cond_init_result = pthread_cond_init(&work_units_cond, NULL);
	if (work_units_cond_init_result == 0) {
	}
	else if (work_units_cond_init_result == EAGAIN) {
		exit(150);
	}
	else if (work_units_cond_init_result == ENOMEM) {
		exit(150);
	}
	else {
		exit(150);
	}
}
void init_voice() {
	int voice_index = -1;
	for (int i = 0; i < 256; i++) {
		if (nodedata_top->voices[i] == 0) {
			voice_index = i;
			break;
		}
	}
	if (voice_index == -1) {
		return ;
	}
	nodedata_top->voices[voice_index] = 1;
	Node_value1_create_voice(nodedata_value1, voice_index);
	Node_value2_create_voice(nodedata_value2, voice_index);
	Node_add_create_voice(nodedata_add, voice_index);
	Node_out_create_voice(nodedata_out, voice_index);
}
void process() {
	process_workunits();
}
int main() {
	int fprintf_mutex_init_result;
	fprintf_mutex_init_result = pthread_mutex_init(&fprintf_mutex, NULL);
	if (fprintf_mutex_init_result == 0) {
	}
	else if (fprintf_mutex_init_result == EAGAIN) {
		exit(150);
	}
	else if (fprintf_mutex_init_result == ENOMEM) {
		exit(150);
	}
	else if (fprintf_mutex_init_result == EPERM) {
		exit(150);
	}
	else {
		exit(150);
	}
	init_threads();
	init_workunits();
	init_nodes();
	init_voice();
	for (long i = 0; ; i++) {
		process_communication();
		process();
		if (i % 10000 == 0)
			printf("%lu\n", i);
	}
	return 0;
}
