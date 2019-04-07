package net.merayen.elastic.backend.conversion.ffmpeglinux

import net.merayen.elastic.backend.conversion.ConversionModule
import net.merayen.elastic.backend.conversion.FileInfo
import java.io.File

/**
 * Handles conversion of video files.
 * These will probably be used just for reference, when making music to movies etc.
 * It might import just the video, encoding it to some low-bitrate 320x240 or similar, to not take too much space inside the project.
 * Not implemented for now.
 */
class FFmpegVideo(files: Array<File>, settings: Settings) : ConversionModule(files, settings) {
    override fun process(): Array<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun retrieveInfo(): Array<FileInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProgress(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Settings : ConversionModule.Settings()
}