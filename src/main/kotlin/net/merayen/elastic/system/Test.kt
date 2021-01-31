package net.merayen.elastic.system

import net.merayen.elastic.backend.architectures.local.JavaLocalDSPBackend
import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.system.intercom.BackendReadyMessage
import net.merayen.elastic.system.intercom.CreateDefaultProjectMessage
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.ui.JavaUI
import java.io.File

class Test private constructor() {
	private val system = ElasticSystem(
		File("NewProject.elastic").absolutePath,
		uiModule = JavaUI::class,
		dspModule = JavaLocalDSPBackend::class,
		backendModule = JavaBackend::class
	)

	private var hasNodes = false
	private var hasStarted = false

	private val tap = system.listenToMessagesFromBackend {
		if (it is BackendReadyMessage)
			hasStarted = true

		if (it is CreateNodeMessage)
			hasNodes = true
	}

	internal var start = System.currentTimeMillis()

	internal inner class Roflmao {
		var t: Long = 0
	}

	init {
		waitFor { hasStarted }

		if (!hasNodes) {
			system.send(CreateDefaultProjectMessage())
		}

		val roflmao = Roflmao()

		// Run "forever", project should be identical to the one created at the beginning
		while (true) {
			println("Saving checkpoint")
			system.send(CreateCheckpointMessage())
			roflmao.t = System.currentTimeMillis() + 30000 * 1
			waitFor {
				System.currentTimeMillis() > roflmao.t
			}
		}

		tap.close()
	}

	private fun waitFor(func: () -> Boolean) {
		try {
			while (!func()) {
				system.update(1000)
				//Thread.sleep(1)
			}
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}
	}

	companion object {
		fun test() {
			Test()
		}
	}
}
