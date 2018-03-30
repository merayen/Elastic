package net.merayen.elastic.ui

import java.util.ArrayList

import org.json.simple.JSONObject

import net.merayen.elastic.ui.controller.Gate
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.util.DrawContext
import net.merayen.elastic.util.Postmaster

/**
 * This runs in the main-thread (???), and represents everything UI, at least locally.
 * (Remote UI might be an option later on)
 */
class Supervisor(private val handler: Handler) {
    internal var surfacehandler: SurfaceHandler
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
        fun onMessageToBackend(message: Postmaster.Message)

        /**
         * Called by the UI-thread when ready to receive message.
         * Call Supervisor().sendMessageToUI(...) in a loop until you have no messages.
         */
        fun onReadyForMessages()
    }

    init {
        surfacehandler = SurfaceHandler(this)
        top = Top(surfacehandler)

        val self = this
        gate = Gate(top, Gate.Handler { message -> self.handler.onMessageToBackend(message) })

        ui_gate = gate.uiGate
        top.setUIGate(ui_gate)
        backend_gate = gate.backendGate
    }

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
        uiobject.absolute_translation = dc.translation_stack.absolute

        if (!uiobject.isInitialized)
            uiobject.initialize()

        val draw = Draw(uiobject, dc)

        uiobject.updateDraw(draw)

        uiobject.outline_abs_px = draw.absoluteOutline
        uiobject.outline = draw.outline

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

    fun sendMessageToUI(message: Postmaster.Message) {
        backend_gate.send(message)
    }

    fun end() {
        surfacehandler.end()
    }

    fun dump(): JSONObject {
        return gate.dump()
    }
}
