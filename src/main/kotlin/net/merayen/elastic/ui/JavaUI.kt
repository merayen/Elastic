package net.merayen.elastic.ui

import net.merayen.elastic.system.UIModule
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.util.DrawContext

class JavaUI : UIModule() {
	private val surfaceHandler: SurfaceHandler = SurfaceHandler()
	private val top = Top(surfaceHandler)
	private val supervisor = Supervisor(top)

	init {
		surfaceHandler.handler = object : SurfaceHandler.Handler {
			override fun onDraw(drawContext: DrawContext) {
				supervisor.draw(drawContext)
			}
		}
	}

	override fun onInit() {}

	override fun onUpdate() {
		while (isRunning) { // We never return, holding the loop
			for (message in ingoing.receiveAll())
				top.sendMessageToUI(message)

			val messages = top.retrieveMessagesFromUI()
			if (messages.isNotEmpty()) {
				outgoing.send(messages)
				notifyElasticSystem()
			}

			sleep(1) // TODO should probably sleep thread somehow?
		}
	}

	override fun onEnd() {
		surfaceHandler.end()
	}
}