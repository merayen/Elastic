package net.merayen.elastic.ui.objects.top

import java.util.ArrayList
import java.util.Collections

import net.merayen.elastic.ui.SurfaceHandler
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.Gate.UIGate
import net.merayen.elastic.ui.objects.top.mouse.MouseCursorManager
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.ui.util.NativeUI
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.UniqueID

/**
 * The very topmost object.
 * Holds track of all the windows (called surfaces), and which to draw in which context.
 */
class Top(private val surfacehandler: SurfaceHandler) : UIObject() {
	val mouseCursorManager = MouseCursorManager()
	private val windows = ArrayList<Window>()
	private var ui_gate: UIGate? = null

	val nativeUI = NativeUI()

	init {
		createWindow()
		add(mouseCursorManager)
	}

	/**
	 * We override this method to return the correct UIObject for the window being drawn.
	 * TODO decide which Window()-object to return upon DrawContext-type.
	 */
	override fun onGetChildren(surface_id: String): List<UIObject> {
		val result = ArrayList<UIObject>()

		for (w in windows) {
			if (w.surfaceID == surface_id) {
				result.add(w)
				break
			}
		}

		if(result.isEmpty())
			println("Window ID not found")

		result.add(mouseCursorManager)

		return result
	}

	fun setUIGate(ui_gate: UIGate) {
		this.ui_gate = ui_gate
	}

	fun getWindows(): List<Window> {
		return Collections.unmodifiableList(windows)
	}

	fun createWindow() {
		val w = Window(surfacehandler.createSurface(UniqueID.create()))
		windows.add(w)
		add(w)
	}

	override fun sendMessage(message: Postmaster.Message) {
		ui_gate!!.send(message)
	}
}