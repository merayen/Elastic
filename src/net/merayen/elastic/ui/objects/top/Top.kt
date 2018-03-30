package net.merayen.elastic.ui.objects.top

import java.util.ArrayList
import java.util.Collections

import net.merayen.elastic.ui.SurfaceHandler
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.Gate.UIGate
import net.merayen.elastic.ui.surface.Surface
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.UniqueID

/**
 * The very topmost object.
 * Holds track of all the windows (called surfaces), and which to draw in which context.
 */
class Top(private val surfacehandler: SurfaceHandler) : UIObject() {
    private val windows = ArrayList<Window>()
    internal var ui_gate: UIGate? = null

    init {
        createWindow()
    }

    override fun onInit() {}

    /**
     * We override this method to return the correct UIObject for the window being drawn.
     * TODO decide which Window()-object to return upon DrawContext-type.
     */
    override fun onGetChildren(surface_id: String): List<UIObject> {
        for (w in windows)
            if (w.surfaceID == surface_id)
                return object : ArrayList<UIObject>() {
                    init {
                        add(w)
                    }
                }

        println("Window ID not found")
        return ArrayList()
    }

    fun setUIGate(ui_gate: UIGate) {
        this.ui_gate = ui_gate
    }

    fun getWindows(): List<Window> {
        return Collections.unmodifiableList(windows)
    }

    private fun createSurface(id: String): Surface {
        return surfacehandler.createSurface(id)
    }

    fun createWindow() {
        val w = Window(createSurface(UniqueID.create()))
        windows.add(w)
        add(w)
    }

    public override fun sendMessage(message: Postmaster.Message) {
        ui_gate!!.send(message)
    }
}
