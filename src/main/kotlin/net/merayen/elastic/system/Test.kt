package net.merayen.elastic.system

import net.merayen.elastic.system.actions.LoadProject
import net.merayen.elastic.system.actions.NewProject
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.StartBackendMessage
import java.io.File

class Test private constructor() {

	private val system = ElasticSystem()

	internal var fires: Int = 0
	internal var start = System.currentTimeMillis()
	internal var ispinne: Long = 0

	internal inner class Roflmao {
		var t: Long = 0
	}

	init {
		val file = File(PROJECT_PATH)
		if (file.exists()) {
			system.runAction(LoadProject(PROJECT_PATH))
		} else {
			system.runAction(NewProject(PROJECT_PATH))
		}

		val roflmao = Roflmao()

		system.sendMessageToBackend(listOf(StartBackendMessage()))

		// Run "forever", project should be identical to the one created at the beginning
		while (true) {
			roflmao.t = System.currentTimeMillis() + 1000 * 60
			waitFor(object : Func {
				override fun noe(): Boolean {
					return System.currentTimeMillis() > roflmao.t
				}
			})
			println("Saving checkpoint")
			system.sendMessageToBackend(listOf(CreateCheckpointMessage()))
		}
	}

	internal interface Func {
		fun noe(): Boolean
	}

	private fun waitFor(func: Func) {
		try {
			while (!func.noe()) {
				system.update()
				Thread.sleep(0)
			}
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}

	}

	companion object {
		fun test() {
			Test()
		}

		private val PROJECT_PATH = File("NewProject.elastic").absolutePath
	}
}
