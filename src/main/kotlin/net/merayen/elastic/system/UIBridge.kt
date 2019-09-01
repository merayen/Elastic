package net.merayen.elastic.system

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.Supervisor
import net.merayen.elastic.ui.SurfaceHandler
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.util.DrawContext

class UIBridge {
	interface Handler {
		fun onMessageToBackend(message: ElasticMessage)
	}

	private val surfaceHandler: SurfaceHandler = SurfaceHandler()
	private val top = Top(surfaceHandler)
	private val supervisor: Supervisor

	var handler: Handler? = null

	init {
		val self = this
		supervisor = Supervisor(top)

		/*top.handler = object : Top.Handler {
			override fun onSendMessage(message: ElasticMessage) {
				gate.uiGate.send(message)
			}
		}*/

		surfaceHandler.handler = object : SurfaceHandler.Handler {
			override fun onDraw(drawContext: DrawContext) {
				supervisor.draw(drawContext)
			}
		}
	}

	fun retrieveMessagesFromUI() = top.retrieveMessagesFromUI()

	fun sendMessagesToUI(messages: Collection<ElasticMessage>) = top.sendMessagesToUI(messages)

	fun end() {
		surfaceHandler.end()
	}
}