package net.merayen.elastic.ui.objects.top

import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.SurfaceHandler
import net.merayen.elastic.ui.TopNode
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.controller.*
import net.merayen.elastic.ui.objects.top.mouse.MouseCursorManager
import net.merayen.elastic.ui.surface.Swing
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.UniqueID
import java.util.*

/**
 * The very topmost object.
 * Holds track of all the windows (called surfaces), and which to draw in which context.
 */
class Top(private val surfaceHandler: SurfaceHandler) : UIObject(), TopNode {
	val mouseCursorManager = MouseCursorManager()
	private val windows = ArrayList<Window>()

	private val controllers = ArrayList<Controller>()

	private val messagesToBackend = Postmaster<ElasticMessage>()
	private val messagesToUI = Postmaster<ElasticMessage>()

	val netlist = NetList() // FIXME should the netlist really be global for everything? Perhaps yes?

	init {
		add(mouseCursorManager)

		controllers.add(SplashViewController(this))
		controllers.add(NetListController(this))
		controllers.add(ViewportController(this))
		controllers.add(NodeViewController(this))
		controllers.add(EditNodeController(this))
		controllers.add(ArrangementController(this))
		controllers.add(StatisticsReportController(this))

		createWindow() // First window
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

		if (result.isEmpty())
			println("Window ID not found")

		result.add(mouseCursorManager)

		return result
	}

	fun getWindows(): List<Window> {
		return Collections.unmodifiableList(windows)
	}

	private fun createWindow() {
		val w = Window(surfaceHandler.createSurface(UniqueID.create(), Swing::class))
		windows.add(w)
		add(w)
	}

	override fun onUpdate() {
		surfaceHandler.assertUIThread() // Safety trap

		val messages = messagesToUI.receiveAll()

		if (messages.isNotEmpty())
			updateNetList(messages)
	}

	override fun onDraw(draw: Draw) {
		surfaceHandler.assertUIThread() // Safety trap
	}

	/**
	 * Send message from UI to backend.
	 * Called by other UIObjects.
	 * All messages goes through the controllers that may or may not forward the messages.
	 * Some Controllers has local loop-back, meaning that they will not forward messages but respond to them locally.
	 */
	override fun sendMessage(message: ElasticMessage) {
		for (c in controllers) {
			c.onMessageFromUI(message)
			messagesToBackend.send(c.retrieveMessagesToBackend())
		}
	}

	/**
	 * Called by e.g UIBridge
	 */
	override fun retrieveMessagesFromUI() = messagesToBackend.receiveAll()

	/**
	 * Called by e.g UIBridge
	 */
	override fun sendMessageToUI(message: ElasticMessage) = messagesToUI.send(message)

	private fun updateNetList(messages: Collection<ElasticMessage>) {
		for (message in messages) {
			NetListMessages.apply(netlist, message)

			for (c in controllers)
				c.onMessageFromBackend(message)
		}
	}
}