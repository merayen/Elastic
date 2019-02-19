package net.merayen.elastic.ui

import java.util.HashMap
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.ui.util.DrawContext

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

			val surface = Swing(id, Surface.Handler { graphics2d ->
				// TODO Hardcoded Swing as that is the only one we support for now
				val currentEvents = surfaces[id]!!.pullEvents()
				synchronized(lock) {
					try {
						//println("1")
						if (drawing) {
							//println("2")
							//return@Handler
							throw RuntimeException("UI can only be run by a single thread. You may have invoked Swing-code that has spawned another thread to draw?")
						}
						drawing = true
						//println("3")
						supervisor.draw(DrawContext(graphics2d, surfaces[id], currentEvents))
					} finally {
						//println("4")
						drawing = false
					}

					supervisor.runPostDrawJobs()
				}
			})

			surfaces[id] = surface

			return surface
		}
	}
}
