package net.merayen.elastic.backend.conversion

import net.merayen.elastic.backend.conversion.ffmpeglinux.FFmpegAudio
import kotlin.reflect.KClass

enum class ConversionModuleRegistry(val cls: KClass<out ConversionModule>) {
    FFMPEG(FFmpegAudio::class)
}