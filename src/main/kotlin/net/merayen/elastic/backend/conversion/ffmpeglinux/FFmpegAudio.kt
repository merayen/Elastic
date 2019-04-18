package net.merayen.elastic.backend.conversion.ffmpeglinux

import net.merayen.elastic.backend.conversion.AudioFileInfo
import net.merayen.elastic.backend.conversion.ConversionError
import net.merayen.elastic.backend.conversion.ConversionModule
import net.merayen.elastic.backend.conversion.FileInfo
import net.merayen.elastic.util.UniqueID
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Handles conversion of files to audio that can be used by Elastic.
 */
class FFmpegAudio(inputFiles: Array<File>, settings: Settings) : ConversionModule(inputFiles, settings) {
    private var progress = 0f

    override fun getProgress(): Float {
        return 1f
    }

    override fun retrieveInfo(): Array<FileInfo> {
        val result = ArrayList<FileInfo>()
        var i = 0

        for (file in inputFiles)
            result.add(getInfoForFile(file))

        return result.toTypedArray()
    }

    override fun process(): Array<File> {
        val result = ArrayList<File>()

        var i = 0
        for (file in inputFiles) {
            result.add(processFile(file))

            i++
            progress = i / inputFiles.size.toFloat()
        }

        return result.toTypedArray()
    }

    private fun getInfoForFile(file: File): FileInfo {
        val process = Runtime.getRuntime().exec(arrayOf("ffprobe", "-i", file.absolutePath, "-hide_banner", "-show_format", "-show_streams", "-of", "json", "-v", "error"))
        process.waitFor() // TODO if ffprobe hangs, handle it
        val result = String(process.inputStream.readBytes())

        if (process.exitValue() != 0)
            throw ConversionError("Could not get info of file ${file.absolutePath}, ffprobe says: ${String(process.errorStream.readBytes())}")

        val info = JSONParser().parse(result) as? JSONObject ?: throw ConversionError("Could not read JSON from ffprobe: $result")
        val streams = info["streams"] as? JSONArray ?: throw ConversionError("Could not read streams-section from ffprobe: $result")

        val durationString = ((info["format"] as? JSONObject)?.get("duration") as? String) ?: throw ConversionError("Could not extract duration of the media. ffprobe says: $result")
        val duration = durationString.toFloat()

        var sampleRate: Int? = null
        var channels: Int? = null

        for (x in streams) {
            val stream = x as? JSONObject ?: throw ConversionError("Expected stream-object to be a JSONObject. ffprobe says: $result")
            if (stream["codec_type"] == "audio") { // Only supports audio for now

                if (sampleRate != null)
                    throw ConversionError("Multiple audio tracks not supported yet: ffprobe says: $result")

                val sampleRateString = stream["sample_rate"] as? String ?: throw ConversionError("Expected sample rate to be a String. ffprobe says: $result")
                sampleRate = sampleRateString.toInt()

                channels = (stream["channels"] as? Long ?: throw ConversionError("Expected channel count to be a String. ffprobe says: $result")).toInt()
            }
        }

        if (sampleRate == null || channels == null)
            throw ConversionError("No audio was found in the file. ffprobe says: $result")

        return AudioFileInfo(file.absolutePath, duration, sampleRate, channels)
    }

    private fun processFile(file: File): File {
        val destFilePath = "/tmp/${UniqueID.create()}.wav"

        val process = Runtime.getRuntime().exec(arrayOf("ffmpeg", "-i", file.absolutePath, "-hide_banner", "-nostdin", destFilePath))
        process.waitFor(30, TimeUnit.SECONDS) // Just 30 seconds? May work for audio, but what about video?
        if (process.isAlive) {
            process.destroyForcibly()
            throw ConversionError("FFmpeg used more than 30 seconds")
        }

        val result = process.inputStream.readBytes()
        val error = process.errorStream.readBytes()

        if (process.exitValue() != 0)
            throw ConversionError("FFmpeg returned with exit code ${process.exitValue()}: ${String(error)}")

        return File(destFilePath)
    }

    companion object {
        class Settings : ConversionModule.Settings() // TODO settings for sample rate to convert to etc
    }
}