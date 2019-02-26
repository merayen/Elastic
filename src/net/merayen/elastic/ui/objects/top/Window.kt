package net.merayen.elastic.ui.objects.top

import net.merayen.elastic.Config
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.mouse.MouseCursor
import net.merayen.elastic.ui.objects.top.mouse.SurfaceMouseCursors
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.util.Point

/**
 * The very topmost UIObject for a Window, containing all the UI for that window.
 * Represents a certain UIData in a certain Node-group where it gets all the properties from, like window-size.
 */
class Window(private val surface: Surface) : UIObject() {

	// Cached screen layoutWidth and layoutHeight. Updates on every draw. Children UIObjects can use this to get screen size in pixels
	var screenWidth = 0f
		private set
	var screenHeight = 0f
		private set

	var surfaceLocation = Point()
		private set

	val nativeUI = surface.nativeUI

	/**
	 * Windows and other popups can be put here.
	 * UIObjects put here are put on top of everything.
	 */
	val overlay = UIObject()
	val surfaceMouseCursors = SurfaceMouseCursors()
	val debug = Debug()

	// Note, need to allow multiple viewports when having several windows?
	val viewportContainer = ViewportContainer()

	val surfaceID: String
		get() = surface.id

	init {
		add(viewportContainer)

		if(Config.ui.debug.overlay)
			initDebug()

		add(overlay)
		add(surfaceMouseCursors)
	}

	override fun onInit() {
		(search.top as Top).mouseCursorManager.addSurface(surfaceMouseCursors)
	}

	private fun initDebug() {
		debug.translation.y = 40f
		debug.translation.scale_x = .1f
		debug.translation.scale_y = .1f
		add(debug)
		debug.set("DEBUG", "Has been enabled")
	}

	override fun onDraw(draw: Draw) {
		screenWidth = draw.screenWidth.toFloat()
		screenHeight = draw.screenHeight.toFloat()
		surfaceLocation = draw.surfaceLocation
	}

	override fun onUpdate() {
		viewportContainer.width = screenWidth
		viewportContainer.height = screenHeight - 10
	}
}