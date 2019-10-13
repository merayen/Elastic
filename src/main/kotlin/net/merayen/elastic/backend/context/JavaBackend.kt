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
		var synchronization: Synchronization,
		var project: Project,
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

	private fun createEnvironment(): Environment {
		val mixer = Mixer()
		val sync = Synchronization(mixer, object : Synchronization.Handler {
			override fun needData() {
				// Send the message into the usual queue so that it gets run in the correct thread
				ingoing.send(ProcessRequestMessage())
			}

			override fun behind() {}
		})

		return Environment(mixer, sync, Project(projectPath))
	}

	override fun mainLoop() {
		// Restore project
		ingoing.send(NetListMessages.disassemble(environment.project.data.rawNetList))
		handleMessages()

		outgoing.send(BackendReadyMessage()) // Notifies that backend is ready and that the project has been restored

		environment.synchronization.start()

		while (isRunning) {
			handleMessages()
			sleep(1)
		}

		environment.synchronization.end()
	}

	/**
	 * Send message to backend
	 */
	private fun handleMessages() { // TODO soon: check that all messages are treated correctly
		while (true) {
			when (val message = ingoing.receive() ?: return) {
				is CreateCheckpointMessage -> environment.project.checkpoint.create()
				is TidyProjectMessage -> environment.project.tidy()
				is ImportFileIntoNodeGroupMessage -> ImportFileIntoNodeGroup(message).run()
				else -> backendSupervisor.receiveMessage(message)
			}
		}
	}

	private fun createLogicNodeBackend() = Supervisor(environment, object : Supervisor.Handler {
		override fun onSendToDSP(message: ElasticMessage) = outgoing.send(message)
		override fun onSendToUI(message: ElasticMessage) = outgoing.send(message)

		override fun onProcessDone() {
			environment.synchronization.push(
				environment.configuration.sampleRate,
				environment.configuration.bufferSize
			)
			environment.mixer.dispatch(environment.configuration.bufferSize)
		}
	})
}
