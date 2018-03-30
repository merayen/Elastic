package net.merayen.elastic.ui

import java.util.HashMap

import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.ui.util.DrawContext

class SurfaceHandler internal constructor(private val supervisor: Supervisor) {
    private val surfaces: MutableMap<String, Surface> = HashMap() // A surface is usually a window

    fun end() {
        for (s in surfaces.values)
            s.end()
    }

    fun createSurface(id: String): Surface {
        synchronized(surfaces) {
            if (surfaces.containsKey(id))
                throw RuntimeException("Surface with that id already exists")

            val surface = Swing(id, Surface.Handler { graphics2d ->
                // TODO Hardcoded Swing as that is the only one we support for now
                val current_events = surfaces[id]!!.pullEvents()
                supervisor.draw(DrawContext(graphics2d, surfaces[id], current_events))
            })

            surfaces[id] = surface

            return surface
        }
    }
}
