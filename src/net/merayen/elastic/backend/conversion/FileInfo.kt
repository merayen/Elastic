package net.merayen.elastic.backend.conversion

abstract class FileInfo(val filePath: String, val duration: Float)

class AudioFileInfo(filePath: String, duration: Float, val sampleRate: Int, val channels: Int) : FileInfo(filePath, duration)