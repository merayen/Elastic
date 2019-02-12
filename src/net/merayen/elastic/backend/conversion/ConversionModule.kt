package net.merayen.elastic.backend.conversion

import java.io.File

abstract class ConversionModule(protected val inputFiles: Array<File>, protected val settings: Settings) { // TODO Better name
    abstract class Settings

    /**
     * Process the queue. Is being done async.
     */
    abstract fun process(): Array<File>

    /**
     * Retrieve info from the inputs.
     * Should perhaps be standalone?
     */
    abstract fun retrieveInfo(): Array<FileInfo>

    /**
     * Retrieve the current progress.
     * Number in the range of 0 (beginning) to 1 (finished)
     */
    abstract fun getProgress(): Float
}