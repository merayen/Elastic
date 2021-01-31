package net.merayen.elastic.backend.queue

/**
 * Item in the queue.
 * @param sequence Which sequence this QueueTask belongs to. Only 1 task can be run at once in a sequence
 */
abstract class QueueTask(val sequence: Any? = null) {
	enum class QueueTaskState {
		QUEUED, PROCESSING, FINISHED, FAILED, CANCELLED
	}


	/**
	 * The current state of the task
	 */
	@Volatile
	var _state = QueueTaskState.QUEUED


	/**
	 * Set by Queue(). Don't touch.
	 */
	internal var _queue: Queue? = null


	/**
	 * Set by Queue(). Don't touch.
	 */
	internal var _thread: Thread? = null

	var exception: Throwable? = null


	/**
	 * Begin the processing. Only to be called by Queue
	 */
	fun process() {
		if (_state != QueueTaskState.PROCESSING)
			throw RuntimeException("QueueTask must be in the state QUEUED, not $_state")

		try {
			onProcess()
		} catch (exception: Throwable) {
			_state = QueueTaskState.FAILED
			this.exception = exception
			_queue!!.update()

			exception.printStackTrace()

			onCleanup()

			return
		}

		_state = QueueTaskState.FINISHED

		_queue!!.update()

		onCleanup()
	}


	/**
	 * Ask the task to cancel.
	 */
	fun cancel() {
		try {
			onCancel()
		} catch (exception: Throwable) {
			_state = QueueTaskState.FAILED
			this.exception = exception
			return
		}

		_state = QueueTaskState.CANCELLED

		_queue!!.update()

		onCleanup()
	}


	/**
	 * Implement your processing logic here
	 */
	abstract fun onProcess()


	/**
	 * Will be called when wanted to cancel the task.
	 */
	abstract fun onCancel()


	/**
	 * Put all your cleanup code here. Will be run when finished, after getting cancelled, and when throwing an exception
	 */
	abstract fun onCleanup()
}