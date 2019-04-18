package net.merayen.elastic.backend.queue

import java.util.ArrayDeque
import kotlin.collections.HashSet

/**
* An async queue with support for progress reporting.
 */
class Queue(val threadCount: Int) {
    private val tasks = ArrayDeque<QueueTask>()
    private val threads = ArrayDeque<Thread>()

    @Synchronized
    fun addTask(task: QueueTask) {
        task._queue = this
        tasks.add(task)
        update()
    }

    /**
     * Runs an update, starting any waiting tasks.
     * Not needed to be run manually, as it gets run when adding new tasks, when tasks ends etc
     */
    @Synchronized
    fun update() {
        val activeSequences = calcActiveSequences()

        var retry = true
        while (retry) {
            retry = false

            loop@ for (task in tasks) {
                val state = task._state
                if (
                        state == QueueTask.QueueTaskState.FINISHED ||
                        state == QueueTask.QueueTaskState.FAILED ||
                        state == QueueTask.QueueTaskState.CANCELLED
                ) {
                    tasks.remove(task)
					threads.remove(task._thread)
					retry = true
					break@loop

                } else if (state == QueueTask.QueueTaskState.QUEUED) {
                    if (task.sequence == null || task.sequence !in activeSequences) {
                        if (threads.size < threadCount) {
                            task._state = QueueTask.QueueTaskState.PROCESSING
                            val thread = Thread {
                                task.process()
                            }
                            threads.add(thread)
                            task._thread = thread
                            thread.start()
                        }
                    }
                }
            }
        }
    }

    private fun calcActiveSequences(): HashSet<Any> {
        val result = HashSet<Any>()

        for (task in tasks)
            if (task.sequence != null && task._state == QueueTask.QueueTaskState.PROCESSING)
                result.add(task.sequence)

        return result
    }

    /**
     * Waits until all tasks are done. Will allow queueing new ones while joining.
     */
    @Synchronized
    fun join() {
        while (threads.size > 0)
            for (thread in threads)
                thread.join()
    }
}