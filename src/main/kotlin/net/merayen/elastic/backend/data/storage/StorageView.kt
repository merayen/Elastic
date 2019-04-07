package net.merayen.elastic.backend.data.storage

interface StorageView : AutoCloseable {
    fun readFile(path: String): StorageFile
    fun writeFile(path: String): StorageFile
    fun exists(path: String): Boolean
    fun list(path: String): List<String>
    fun listAll(path: String): List<String>
    override fun close()
}
