package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Code for scheduling tasks (C methods) to be run later in threads.
 *
 * This is for performance and use of several CPU cores.
 */
internal class QueueComponent(
	private val threadCount: Int,
	private val workUnitsComponent: WorkUnitsComponent,
	log: LogComponent,
	debug: Boolean
) : TranspilerComponent(log, debug) {
	private val threadMutexes = PThreadMutex("", log, debug)
	private val threadConds = PThreadCond("", log, debug)

	fun writeDefinition(codeWriter: CodeWriter) {
		with(codeWriter) {
			Struct("threads_t", listOf("threads[$threadCount]")) {
				Member("pthread_t", "thread")
				Member("pthread_cond_t", "cond")
				Member("pthread_cond_t", "cond_started")
				Member("pthread_mutex_t", "lock") // Only the one holding this lock should write to this struct
				Member("volatile void*", "func") // Method to run
				Member("volatile bool", "inited")
			}
		}
	}

	fun writeMethods(codeWriter: CodeWriter) {
		with(codeWriter) {
			// Will run a function with the argument "voice". Will poll several threads or just block if no threads available
			Method("void", "queue_task", "void *func") {
				writeLog(this, "queue_task %p", "func")
				Statement("bool lock_success = false")
				workUnitsComponent.writeLock(codeWriter) {
					For("int i = 0", "i < $threadCount", "i++") {
						threadMutexes.writeTryLock(this, variableExpression = "threads[i].lock", locked = {
							If("threads[i].func == NULL") {
								// Found a thread that is available
								Statement("lock_success = true")
								writeLog(this, "queue_task: Found slot to queue %p into [%d]", "func, i")
								Statement("threads[i].func = func")
								threadConds.writeCallBroadcast(this, "&threads[i].cond")
								writeLog(this, "queue_task: Queued %p", "func")
							}
						})
						If("lock_success") {
							Break()
						}
					}
				}
				If("lock_success") {
					Return()
				}
				writeLog(this, "queue_task: No empty thread found queueing %p, waiting", "func")
				Call("((void(*)())func)")
			}

			Method("void*", "thread_runner", "void *args")
			{ // Run by each thread
				Statement("int thread_index = *((int *)args)")

				If("thread_index < 0 || thread_index >= $threadCount") {
					Call("exit", "EXIT_FAILURE")
				}

				Statement("struct threads_t* thread = &threads[thread_index]")

				writeLog(this, "[%i] Launched", "thread_index")
				Call("pthread_mutex_lock", "&thread->lock") // Lock to us all the time, as long as we do not pthread_cond_wait

				While("true") {
					Statement("thread->inited = true")

					Call("pthread_cond_broadcast", "&thread->cond")
					If("thread->func == NULL") { // No job set, go back to sleep
						writeLog(this, "[%i] No job set, waiting for work", "thread_index")
						threadConds.writeWait(codeWriter, threadMutexes, "&thread->cond", "&thread->lock")
						//Call("pthread_cond_wait", "&thread->cond, &thread->lock") // Wait for someone to wake us up, also temporary unlocks the mutex
						Continue() // Woken up, look for job again
					}

					writeLog(this, "Thread %i has gotten work", "thread_index")
					Call("((void(*)())thread->func)")
					Statement("thread->func = NULL")
				}
			}

			Method("void", "init_threads")
			{
				// Clear memory
				Call("memset", "threads, 0, sizeof(threads)")

				// Set up conditions, that we use to make the threads sleep
				For("int i = 0", "i < $threadCount", "i++") {
					Call("pthread_cond_init", "&threads[i].cond_started, NULL")
					Call("pthread_cond_init", "&threads[i].cond, NULL")
				}

				// Init mutexes
				For("int i = 0", "i < $threadCount", "i++") {
					if (debug) {
						Statement("pthread_mutexattr_t mutex_attr")
						Call("memset", "&mutex_attr, 0, sizeof(pthread_mutexattr_t)")

						If("pthread_mutexattr_settype(&mutex_attr, PTHREAD_MUTEX_ERRORCHECK) != 0") {
							panic(codeWriter);
						}
						Call("pthread_mutex_init", "&threads[i].lock, &mutex_attr")
					} else {
						Call("pthread_mutex_init", "&threads[i].lock, NULL")
					}
				}

				// Create and run the threads
				For("int i = 0", "i < $threadCount", "i++") {
					Statement("int *thread_index = malloc(sizeof(int))")
					Statement("*thread_index = i")

					//fprintf(stderr, "[main:%i] Locking\n", i);
					Call("pthread_mutex_lock", "&threads[i].lock") // Lock thread before running it
					Call("pthread_create", "&threads[i].thread, NULL, thread_runner, thread_index")
				}

				// Wait for all threads to have inited
				For("int i = 0", "i < $threadCount", "i++") {
					//fprintf(stderr, "[main:%i] Waiting for thread to start\n", i);
					While("true") {
						Call("pthread_cond_wait", "&threads[i].cond, &threads[i].lock")

						//fprintf(stderr, "[main:%i] Woke up\n", i);

						If("threads[i].inited") {
							Break()
						}

						//fprintf(stderr, "[main:%i] Not ready yet, trying again\n", i);
					}
					//fprintf(stderr, "[main:%i] Thread reported to have started, unlocking it from main thread\n", i);
					Call("pthread_mutex_unlock", "&threads[i].lock") // Unlock the thread from us
				}
			}
		}
	}
}
