int unit_id_counter = 0;
void unit_funcs[UNIT_COUNT]; // Set when a unit has been processed

int queue_unit() {
	// Queue
}

void wait_unit() {
	
}


void unit1() { // Runs two nodes connected in series
	node1_process();
	node2_process(); // Depends on node1
}

void unit2() {
	node3_process();
}

void unit3() { // Depends on unit1 and unit2 to be finished
	node4_process();
}

// Starts here!
void unit0() { // Run by main-thread
	int unit1_task = queue_unit(unit1); // Run async
	int unit2_task = queue_unit(unit2); // Run async

	wait_unit(unit1_task);
	wait_unit(unit2_task);

	int unit3_task = queue_unit(unit3); 

	wait_unit(unit3_task);
}
