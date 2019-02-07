package net.merayen.elastic.backend.conversion.ffmpeglinux

import net.merayen.elastic.backend.conversion.ConversionModule

class FFmpeg(val settings: Settings) : ConversionModule(settings) {
    override fun process() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class Settings : ConversionModule.Settings()
}