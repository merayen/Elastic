package net.merayen.elastic.ui

import java.util.HashMap
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.ui.util.DrawContext
import java.awt.Graphics2D

class SurfaceHandler internal constructor(private val supervisor: Supervisor) {
    private val surfaces: MutableMap<String, Surface> = HashMap() // A surface is usually a window

    @Volatile
    private var drawing = false

    private val lock = Any()

    fun end() {
        for (s in surfaces.values)
            s.end()
    }

    fun createSurface(id: String): Surface {
        synchronized(surfaces) {
            if (surfaces.containsKey(id))
                throw RuntimeException("Surface with that id already exists")

            val surface = Swing(id, object : Surface.Handler {

                override fun onDraw(graphics2d: Graphics2D) {
                    // TODO Hardcoded Swing as that is the only one we support for now
                    val currentEvents = surfaces[id]!!.pullEvents()
                    try {
                        if (drawing)
                            throw RuntimeException("UI can only be run by a single thread")

                        drawing = true
                        supervisor.draw(DrawContext(graphics2d, surfaces[id], currentEvents))
                    } finally {
                        drawing = false
                    }
                }
            })

            surfaces[id] = surface

            return surface
        }
    }
}
