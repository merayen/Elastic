package net.merayen.elastic.backend.context

import net.merayen.elastic.Temporary
import net.merayen.elastic.backend.data.project.Project
import net.merayen.elastic.backend.mix.Mixer
import net.merayen.elastic.backend.mix.Synchronization
import net.merayen.elastic.backend.nodes.Supervisor
import net.merayen.elastic.backend.queue.Queue
import net.merayen.elastic.system.BackendModule
import net.merayen.elastic.system.actions.ImportFileIntoNodeGroup
import net.merayen.elastic.system.intercom.BackendReadyMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import net.merayen.elastic.system.intercom.backend.TidyProjectMessage
import net.merayen.elastic.util.NetListMessages

/**
 * Glues together NetList, MainNodes and the processing backend (architecture)
 */
class JavaBackend(projectPath: String) : BackendModule(projectPath) {
	/**
	 * Environment is an object that contains common functionality, thrown around in the backend.
	 */
	class Environment(
		val mixer: Mixer,
		val synchronization: Synchronization,
		val project: Project,
		val configuration: Configuration = Configuration(),
		val queue: Queue = Queue(4)
	) {
		class Configuration(
			val sampleRate: Int = Temporary.sampleRate,
			val bufferSize: Int = Temporary.bufferSize,
			val depth: Int = Temporary.depth
		)
	}

	var environment: Environment = createEnvironment()
	private var backendSupervisor = createLogicNodeBackend()

	init {
		name = "JavaBackend"
	}

	private fun createEnvironment(): Environment {
		val mixer = Mixer()
		val sync = Synchronization(mixer, object : Synchronization.Handler {
			override fun needData() {
				// Send the message into the usual queue so that it gets run in the correct thread
				ingoing.send(ProcessRequestMessage())
				schedule()
			}

			override fun behind() {}
		})

		return Environment(mixer, sync, Project(projectPath))
	}

	override fun onInit() {
		// Restore project
		ingoing.send(NetListMessages.disassemble(environment.project.data.rawNetList))
		handleMessages()

		outgoing.send(BackendReadyMessage()) // Notifies that backend is ready and that the project has been restored

		notifyElasticSystem()

		environment.synchronization.start()
	}

	override fun onUpdate() {
		handleMessages()
	}

	override fun onEnd() {
		environment.synchronization.end()
	}

	private fun handleMessages() {
		for (message in ingoing.receiveAll()) {
			when (message) {
				is CreateCheckpointMessage -> environment.project.checkpoint.create()
				is TidyProjectMessage -> environment.project.tidy()
				is ImportFileIntoNodeGroupMessage -> ImportFileIntoNodeGroup(environment, message).run()
				else -> backendSupervisor.receiveMessage(message)
			}
		}
	}

	private fun createLogicNodeBackend() = Supervisor(environment, object : Supervisor.Handler {
		override fun onSendToDSP(message: ElasticMessage) {
			outgoing.send(message)
			notifyElasticSystem()
		}

		override fun onSendToUI(message: ElasticMessage) {
			outgoing.send(message)
			notifyElasticSystem()
		}

		override fun onProcessDone() {
			environment.synchronization.push(
				environment.configuration.sampleRate,
				environment.configuration.bufferSize
			)
			environment.mixer.dispatch(environment.configuration.bufferSize)
		}
	})
}
