package net.merayen.elastic.backend.conversion

import java.io.File
import java.util.ArrayDeque

abstract class ConversionModule(settings: Settings) { // TODO Better name
    abstract class Settings

    protected val queue = ArrayDeque<File>()
    protected val finished = ArrayDeque<File>()

    /**
     * Add a file to the processing queue.
     * Automatically starts processing if
     */
    fun addFile(file: File) {
        queue.add(file)
    }

    /**
     * Retrieve next finished file, if any
     */
    fun popFinishedFile(): File? {
        synchronized(finished) {
            return finished.poll()
        }
    }

    /**
     *
     */
    protected fun markProcessed(file: File) {
        synchronized (queue) {
            queue.remove()
        }

        synchronized(finished) {
            finished.add(file)
        }
    }

    /**
     * Process the queue. Is being done async.
     */
    abstract fun process()
}