package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import java.util.*

abstract class Controller(val top: Top) {
	private val messagesToBackend = Postmaster<ElasticMessage>()

	abstract fun onInit()

	/**
	 * Message received from the backend.
	 */
	abstract fun onMessageFromBackend(message: ElasticMessage)

	/**
	 * Message sent from the UI.
	 */
	abstract fun onMessageFromUI(message: ElasticMessage)

	fun sendToBackend(message: ElasticMessage) = messagesToBackend.send(message)

	fun retrieveMessagesToBackend(): Collection<ElasticMessage> {
		return messagesToBackend.receiveAll()
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

	/**
	 * Use by NodeView to restore itself from the current NetList.
	 */
	fun getNetListRefreshMessages() = NetListMessages.disassemble(top.netlist)
}
