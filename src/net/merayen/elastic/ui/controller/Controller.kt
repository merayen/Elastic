package net.merayen.elastic.ui.controller

import net.merayen.elastic.ui.objects.top.views.View

import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.util.Postmaster

import java.util.ArrayList

abstract class Controller(protected val gate: Gate) {
	val top: Top
		get() = gate.top

	abstract fun onInit()

	/**
	 * Message received from the backend.
	 */
	abstract fun onMessageFromBackend(message: Postmaster.Message)

	/**
	 * Message sent from the UI.
	 */
	abstract fun onMessageFromUI(message: Postmaster.Message)

	/**
	 * Gets run after everything has been draw.
	 */
	abstract fun onAfterDraw()

	fun sendToBackend(message: Postmaster.Message) {
		gate.sendMessageToBackend(message)
	}

	protected fun <T : View> getViews(cls: Class<T>): List<T> {
		val result = ArrayList<T>()

		for (w in top.getWindows())
			if (w.isInitialized)
				for (vp in w.viewportContainer.viewports)
					if (vp.view.javaClass.isAssignableFrom(cls))
						result.add(vp.view as T)

		return result
	}
}
