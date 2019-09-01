package net.merayen.elastic.ui

import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.ui.util.DrawContext
import java.awt.Graphics2D
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class SurfaceHandler {
	interface Handler {
		fun onDraw(drawContext: DrawContext)
	}

	var handler: Handler? = null

	private val surfaces: MutableMap<String, Surface> = HashMap() // A surface is usually a window

	@Volatile
	private var drawing = false

	private val lock = Any()

	fun end() {
		for (s in surfaces.values)
			s.end()
	}

	@Synchronized
	fun createSurface(id: String, cls: KClass<out Surface>): Surface {
		if (surfaces.containsKey(id))
			throw RuntimeException("Surface with that id already exists")

		val surface = cls.primaryConstructor!!.call(id, object : Surface.Handler {
			override fun onDraw(graphics2d: Graphics2D) {
				// TODO Hardcoded Swing as that is the only one we support for now
				val surface = surfaces[id] ?: return

				val currentEvents = surface.pullEvents()
				try {
					if (drawing)
						throw RuntimeException("UI can only be run by a single thread")

					drawing = true
					handler?.onDraw(DrawContext(graphics2d, surface, currentEvents))
				} finally {
					drawing = false
				}
			}
		})

		surfaces[id] = surface

		return surface
	}

	/**
	 * Checks if the current thread is a UI thread.
	 */
	fun isUIThread(): Boolean {
		val id = Thread.currentThread().id
		return surfaces.values.any {it.threadId == id }
	}

	/**
	 * Throws RuntimeException if the caller of the method is not being run inside a UI-thread.
	 * This is for making traps that detects programming errors.
	 */
	fun assertUIThread() {
		if (!isUIThread())
			throw RuntimeException("Code expected to run in UI thread only")
	}
}
