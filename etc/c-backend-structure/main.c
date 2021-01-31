#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>

static long frame_number = 0; // Current frame number
#define FRAME_SIZE 512 // Samples for each frame processed
#define SAMPLE_RATE 44100
#define MAX_PROCESS_COUNT 256

// Top-most node
struct Proc_top1234 {

};

struct Node_top1234 {
	int process_count;
	struct Proc_top1234 *processors[MAX_PROCESS_COUNT];
};

// The value-node
struct Proc_value1234 { // Data for each processor
	float value;
	float port_out[FRAME_SIZE];
};

struct Node_value1234 {
	int process_count;
	struct Proc_value1234 *processors[MAX_PROCESS_COUNT];
	float value;
};


// The out-node
struct Proc_out1234 {
	float output[FRAME_SIZE];
};

struct Node_out1234 {
	int process_count;
	struct Proc_out1234 *processors[MAX_PROCESS_COUNT];
};



struct Data {
	struct Node_top1234 top1234;
	struct Node_value1234 value1234;
	struct Node_out1234 out1234;
} *data;

struct OutData {

};

// Node methods
void Node_top1234_init() {
	
}

void Proc_top1234_init() {

}

void Node_value1234_init() {

}

void Proc_value1234_init() {

}


void init() {
	// Instantiate the processor for the topmost node. They always run no matter what
}

void wait_next_frame() {
	// Wait for Elastic to ask us to process the next frame

	// ...loads incoming data into ourselves (midi played by user, audio samples from a microphone etc, parameter changes ++)

}

/*

A process block that can be run by 1 thread. It 

*/
void process0() {
	// Top-node
	{
		int process_count = data->top1234.process_count;

		// Top-node code
		{
			if (process_count == 0) {
				data->top1234.processors[process_count++] = calloc(1, sizeof(struct Proc_top1234));

				// Instantiate immediate children
				data->value1234.processors[0] = calloc(1, sizeof(struct Proc_value1234));
				data->out1234.processors[0] = calloc(1, sizeof(struct Proc_out1234));
			}
		}

		for (int proc_top1234 = 0; proc_top1234 < process_count; proc_top1234++) { // For each processor of the node top1234
			if (data->top1234.processors[proc_top1234] == 0) continue;

			// value-node
			float port_value1234_out[FRAME_SIZE];
			{
				const int process_count = data->value1234.process_count;
				float value = data->value1234.value;
				for (int proc_value1234 = 0; proc_value1234 < process_count; proc_value1234++) {
					if (data->value1234.processors[proc_value1234] == 0) continue;
					
					for (int i = 0; i < FRAME_SIZE; i++)
						port_value1234_out[i] = value;
				}
			}

			// out-node
			{
				const int process_count = data->out1234.process_count;
				for (int proc_out1234 = 0; proc_out1234 < process_count; proc_out1234++) {
					float *output_data = data->out1234.processors[proc_out1234]->output;
					for (int i = 0; i < FRAME_SIZE; i++)
						output_data[i] = port_value1234_out[i];
				}
			}
		}
	}

	// Send processed data
	// TODO how should we allocate? One huge array that the nodes just add to?
	// Top-node
	{}

	// Value-node
	{}

	//
	{
		
	}
}

/*
Another process unit.
*/
void process1() {
	
}

int main() {
	data = calloc(1, sizeof(struct Data));

	init();

	for (frame_number = 0; frame_number < 1000000L; frame_number++) {
		wait_next_frame();
		process0();
	}

	return 0;
}
