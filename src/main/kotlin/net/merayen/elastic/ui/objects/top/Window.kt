package net.merayen.elastic.ui.objects.top

import net.merayen.elastic.Config
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.ImmutableDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.mouse.SurfaceMouseCursors
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.util.ImmutablePoint

/**
 * The very topmost UIObject for a Window, containing all the UI for that window.
 * Represents a certain UIData in a certain Node-group where it gets all the properties from, like window-size.
 */
class Window(private val surface: Surface) : UIObject(), FlexibleDimension {
	class Overlay : UIObject() {
		private val items = HashMap<UIObject, Long>()
		private var nextPurge = 0L

		override fun add(uiobject: UIObject) = throw RuntimeException("Use register() instead")
		override fun add(uiobject: UIObject, index: Int) = throw RuntimeException("Use register() instead")

		/**
		 * Adds your UIObject to the overlay.
		 * You need to call this method often, as your UIObject will get removed after a certain amount of time
		 * without `use` being called.
		 * This is implemented in case of issues and to prevent blocking overlaying elements.
		 */
		fun use(obj: UIObject) {
			if (obj !in items)
				super.add(obj)

			items[obj] = System.currentTimeMillis()
		}

		/**
		 * Remove your UIObject immediately. It is is not attached (e.g timed out), operation is ignored.
		 */
		override fun remove(uiobject: UIObject) {
			items.remove(uiobject)
			if (uiobject.parent == this)
				remove(uiobject)
		}

		override fun onUpdate() {
			if (nextPurge < System.currentTimeMillis()) {
				val t = System.currentTimeMillis()
				nextPurge = t + 1000

				items.filter { it.value + 1000 < t }.forEach { (k, _) ->
					remove(k)
					items.remove(k)
				}
			}
		}
	}

	override var layoutWidth: Float
		get() = nativeUI.window.size.width
		set(value) {
			nativeUI.window.size = ImmutableDimension(value, nativeUI.window.size.height)
		}

	override var layoutHeight: Float
		get() = nativeUI.window.size.height
		set(value) {
			nativeUI.window.size = ImmutableDimension(nativeUI.window.size.width, value)
		}

	val screenSize: ImmutableDimension
		get() = nativeUI.screen.activeScreenSize

	/**
	 * Current window/surface location.
	 *
	 * Note: To change the size or position of the window itself, check out NativeUI.
	 */
	var surfaceLocation = ImmutablePoint(0f, 0f)
		get() = nativeUI.window.position
		set(value) {
			nativeUI.window.position = ImmutablePoint(value)
			field = nativeUI.window.position
		}

	var isDecorated: Boolean
		get() = nativeUI.window.isDecorated
		set(value) {nativeUI.window.isDecorated = value}

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

		if (Config.ui.debug.overlay)
			initDebug()

		add(overlay)
		add(surfaceMouseCursors)
	}

	override fun onInit() {
		(search.top as Top).mouseCursorManager.addSurface(surfaceMouseCursors)
	}

	private fun initDebug() {
		debug.translation.y = 40f
		debug.translation.scaleX = .1f
		debug.translation.scaleY = .1f
		add(debug)
		debug.set("DEBUG", "Has been enabled")
	}

	override fun onUpdate() {
		val windowSize = nativeUI.window.size
		viewportContainer.width = windowSize.width
		viewportContainer.height = windowSize.height
	}

	/**
	 * Center this window on the screen.
	 */
	fun center() {
		val screenSize = nativeUI.screen.activeScreenSize
		val windowSize = nativeUI.window.size
		nativeUI.window.position = ImmutablePoint(screenSize.width / 2 - windowSize.width / 2, screenSize.height / 2 - windowSize.height / 2)
	}
}