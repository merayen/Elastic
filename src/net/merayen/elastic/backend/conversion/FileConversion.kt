package net.merayen.elastic.backend.conversion

import net.merayen.elastic.backend.conversion.ffmpeglinux.FFmpegAudio
import java.io.File

/**
 * Use this class to converse anything.
 * It will figure out (when implemented) which tool to use for conversion.
 */
class FileConversion {
    companion object {
        class FileConversionResult(val outputFiles: Array<File>, val inputFileInformation: Array<FileInfo>)

        fun convert(inputFiles: Array<File>): FileConversionResult {
            // Uses FFmpeg for now. No logic to check what is available
            val ffmpeg = FFmpegAudio(inputFiles, FFmpegAudio.Companion.Settings())

            return FileConversionResult(ffmpeg.process(), ffmpeg.retrieveInfo())
        }
    }
}

class ConversionError(message: String) : RuntimeException(message)