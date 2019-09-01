package net.merayen.elastic.ui

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SupervisorTest {
	private var supervisor: Supervisor? = null

	@BeforeEach
	fun setUp() {
		supervisor = Supervisor(UIObject())
	}

	@AfterEach
	fun tearDown() {

	}

	@Test
	fun dontCrash() {
		// Just checks if Supervisor can be instantiated, run and then stopped
	}
}