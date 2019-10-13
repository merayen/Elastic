package net.merayen.elastic.system

import net.merayen.elastic.system.actions.CreateDefaultProject
import net.merayen.elastic.system.intercom.CreateDefaultProjectMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.util.tap.ObjectDistributor
import java.io.Closeable
import kotlin.reflect.KClass

/**
 * This class binds together the backend and the UI.
 * This is the top class for everything.
 *
 * Elastic has 3 modules:
 * 	- Backend
 * 	- UI
 * 	- DSP
 *
 * 	This class routes the messages correctly between them
 */
class ElasticSystem(
	projectPath: String,
	uiModule: KClass<out UIModule>,
	dspModule: KClass<out DSPModule>,
	backendModule: KClass<out BackendModule>
) : Closeable {
	private var ui = uiModule.constructors.first().call()
	private var backend = backendModule.constructors.first().call(projectPath)
	private var dsp = dspModule.constructors.first().call()

	private val messagesFromUIDistributor = ObjectDistributor<ElasticMessage>()
	private val messagesFromBackendDistributor = ObjectDistributor<ElasticMessage>()
	private val messagesFromDSPDistributor = ObjectDistributor<ElasticMessage>()

	/**
	 * The thread that made us. We do not allow being used by another
	 */
	private val threadLock = Thread.currentThread().id


	init {
		ui.start()
		dsp.start()
		backend.start()
	}


	/**
	 * Needs to be called often by main thread.
	 * Routes messages between the components.
	 */
	fun update() { // TODO perhaps don't do this, but rather trigger on events
		assertCorrectThread()

		processMessagesFromBackend()
		processMessagesFromUI()
		processMessagesFromDSP()
	}

	private fun processMessagesFromBackend() {
		for (message in backend.outgoing.receiveAll()) {
			// TODO soon: Filter messages
			ui.ingoing.send(message)
			dsp.ingoing.send(message)
			messagesFromBackendDistributor.push(message)
		}
	}

	private fun processMessagesFromUI() {
		for (message in ui.outgoing.receiveAll()) {
			backend.ingoing.send(message)
			messagesFromUIDistributor.push(message)
		}
	}

	private fun processMessagesFromDSP() {
		for (message in dsp.outgoing.receiveAll()) {
			backend.ingoing.send(message)
			messagesFromDSPDistributor.push(message)
		}
	}

	/**
	 * Send messages to Elastic from outside.
	 */
	@Synchronized
	fun send(message: ElasticMessage) {
		assertCorrectThread()

		println("ElasticSystem: $message")

		val backend = backend

		when (message) {
			is CreateDefaultProjectMessage -> {
				runAction(CreateDefaultProject(message))
			}
		}

		backend.ingoing.send(message)
	}

	/**
	 * Only for debugging purposes.
	 * Listen to all messages being sent to the UI.
	 */
	fun listenToMessagesFromUI(func: (item: ElasticMessage) -> Unit) = messagesFromUIDistributor.createTap(func)

	/**
	 * Only for debugging purposes.
	 * Listen to all messages being sent to the backend.
	 */
	fun listenToMessagesFromBackend(func: (item: ElasticMessage) -> Unit) = messagesFromBackendDistributor.createTap(func)

	/**
	 * Only for debugging purposes.
	 * Listen to all messages being sent to the backend.
	 */
	fun listenToMessagesFromDSP(func: (item: ElasticMessage) -> Unit) = messagesFromDSPDistributor.createTap(func)

	private fun runAction(action: Action) {
		action.handler = object : Action.Handler {
			override fun onMessage(message: ElasticMessage) = backend.ingoing.send(message)
			override fun onUpdateSystem() = update()
		}

		// TODO soon: Should we close the tap? What happens here?
		val tap = listenToMessagesFromBackend { action.onMessageFromBackend(it) }.use {
			action.run()
		}
	}

	override fun close() {
		ui.close()
		dsp.close()
		backend.close()

		ui.join()
		dsp.join()
		backend.join()
	}

	private fun assertCorrectThread() {
		if (threadLock != Thread.currentThread().id)
			throw RuntimeException("ElasticSystem can only be used by the thread it was ")
	}
}
