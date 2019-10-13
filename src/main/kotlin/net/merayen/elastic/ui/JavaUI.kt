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

	override fun mainLoop() {
		while (isRunning) {
			for (message in ingoing.receiveAll())
				top.sendMessageToUI(message)

			outgoing.send(top.retrieveMessagesFromUI())

			sleep(1) // TODO should probably sleep thread somehow?
		}
	}

	fun end() = surfaceHandler.end()
}