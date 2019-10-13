package net.merayen.elastic.backend.context

import net.merayen.elastic.system.intercom.CreateNodeMessage
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.io.File

// TODO delete probably. Use ElasticSystemTest instead
internal class JavaBackendTest {
	private val tmpProject = System.getProperty("java.io.tmpdir") + "/" + "BackendTest.elastic"
	private var javaBackend: JavaBackend? = null

	@BeforeEach
	fun setUp() {
		val backend = JavaBackend(tmpProject)
		backend.start()

		this.javaBackend = backend
	}

	@AfterEach
	fun tearDown() {
		val backend = javaBackend!!

		backend.close()

		File(tmpProject).deleteRecursively()
	}

	@Test
	@Timeout(10)
	fun testProjectSetup() {
		val backend = javaBackend!!

		backend.ingoing.send(CreateNodeMessage("group", 1, null))

		var topId: String? = null

		test@ while (true) {
			Thread.sleep(10)
			for (message in backend.outgoing.receiveAll()) {
				if (message is CreateNodeMessage) {
					topId = message.node_id
					break@test
				}
			}
		}

		backend.ingoing.send(CreateNodeMessage("midi", 1, topId))
		backend.ingoing.send(CreateNodeMessage("signalgenerator", 1, topId))
		backend.ingoing.send(CreateNodeMessage("output", 1, topId))
	}
}