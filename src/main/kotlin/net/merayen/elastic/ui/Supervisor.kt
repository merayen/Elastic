package net.merayen.elastic.ui

import net.merayen.elastic.ui.controller.Gate
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.util.DrawContext
import net.merayen.elastic.util.Postmaster
import java.util.*

/**
 * This runs in the main-thread (???), and represents everything UI, at least locally.
 * (Remote UI might be an option later on)
 */
class Supervisor(private val handler: Handler) {
	private var surfacehandler = SurfaceHandler(this)
	private val top: Top
	private val ui_gate: Gate.UIGate // Only to be used by the UI-thread
	private val backend_gate: Gate.BackendGate // Only to be used by the main-thread
	private val gate: Gate

	interface Handler {
		/**
		 * Called by the UI thread. DO NOT DO ANY TIME-CONSUMING STUFF IN THIS CALL.
		 * It will hang the UI.
		 * Rather, queue the message and notify() whatever needs to react on the message.
		 */
		fun onMessageToBackend(message: Any)

		/**
		 * Called by the UI-thread when ready to receive message.
		 * Call Supervisor().sendMessageToUI(...) in a loop until you have no messages.
		 */
		fun onReadyForMessages()
	}

	init {
		top = Top(surfacehandler)

		val self = this
		gate = Gate(top, object: Gate.Handler {
			override fun onMessageToBackend(message: Any) {
				self.handler.onMessageToBackend(message)
			}
		})

		ui_gate = gate.uiGate
		top.setUIGate(ui_gate)
		backend_gate = gate.backendGate
	}

	@Synchronized
	fun draw(dc: DrawContext) {
		internalDraw(dc, top)
		internalUpdate(dc, top)
		internalExecuteIncomingMessages()
		ui_gate.update()
	}

	/**
	 * Drawing of UIObjects. Only allowed to draw, not adding/removing UIObjects etc.
	 */
	private fun internalDraw(dc: DrawContext, uiobject: UIObject) {
		dc.push(uiobject)

		uiobject.draw_z = dc.pushZIndex()
		uiobject.absoluteTranslation = dc.translation_stack.absolute

		if (!uiobject.isInitialized)
			uiobject.initialize()

		val draw = Draw(uiobject, dc)

		uiobject.updateDraw(draw)

		uiobject.outline = draw.outline
		uiobject.absoluteOutline = draw.absoluteOutline

		draw.destroy()

		for (o in uiobject.onGetChildren(dc.surfaceID))
			internalDraw(dc, o)

		dc.pop()
	}

	/**
	 * Non-draw update of UIObjects.
	 * Here the UIObjects can change their properties, add/remove UIObjects, etc.
	 */
	private fun internalUpdate(dc: DrawContext, uiobject: UIObject) {
		if (!uiobject.isAttached && uiobject !== top)
			return

		if (uiobject.isInitialized) { // UIObject probably created in a previous onInit(), and has not been initialized yet, if this skips
			for (e in dc.incoming_events)
				uiobject.onEvent(e)

			uiobject.onUpdate()
		}

		for (o in ArrayList(uiobject.onGetChildren(dc.surfaceID)))
			internalUpdate(dc, o)
	}

	private fun internalExecuteIncomingMessages() {
		handler.onReadyForMessages()
	}

	fun sendMessageToUI(message: Any) {
		backend_gate.send(message)
	}

	fun end() {
		surfacehandler.end()
	}
}
