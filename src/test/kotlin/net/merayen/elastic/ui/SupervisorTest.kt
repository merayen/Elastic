package net.merayen.elastic.ui

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SupervisorTest {
	private var supervisor: Supervisor? = null

	@BeforeEach
	fun setUp() {
		supervisor = Supervisor(object : Supervisor.Handler {
			override fun onMessageToBackend(message: Any) {

			}

			override fun onReadyForMessages() {

			}
		})
	}

	@AfterEach
	fun tearDown() {
		supervisor!!.end()
	}

	@Test
	fun dontCrash() {
		// Just checks if Supervisor can be instantiated, run and then stopped
	}
}