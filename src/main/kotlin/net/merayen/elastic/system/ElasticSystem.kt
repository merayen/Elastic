package net.merayen.elastic.system

import net.merayen.elastic.system.actions.CreateDefaultProject
import net.merayen.elastic.system.intercom.CreateDefaultProjectMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.util.AverageStat
import net.merayen.elastic.util.tap.ObjectDistributor
import java.io.Closeable
import kotlin.math.min
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
	private val lock = Object()
	private var handleMessages = false

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
		val handler = object : ElasticModule.Handler {
			override fun onWakeUp() {
				synchronized(lock) {
					handleMessages = true
					lock.notifyAll()
				}
			}
		}

		ui.handler = handler
		dsp.handler = handler
		backend.handler = handler

		ui.start()
		dsp.start()
		backend.start()
	}

	/**
	 * Routes messages between the components.
	 */
	fun update(timeoutMilliseconds: Long) { // TODO perhaps don't do this, but rather trigger on events
		val start = System.currentTimeMillis() + timeoutMilliseconds
		assertCorrectThread()

		var firstRun = true

		do {
			if (!firstRun) {
				synchronized(lock) {
					if (!handleMessages)
						lock.wait(min(timeoutMilliseconds, 1000))

					handleMessages = false
				}
			}

			processMessagesFromBackend()
			processMessagesFromUI()
			processMessagesFromDSP()

			firstRun = false

		} while (start > System.currentTimeMillis())
	}

	private fun processMessagesFromBackend() {
		var pushed = false
		for (message in backend.outgoing.receiveAll()) {
			pushed = true
			// TODO Filter messages
			ui.ingoing.send(message)
			dsp.ingoing.send(message)
			messagesFromBackendDistributor.push(message)
		}

		if (pushed)
			dsp.schedule()
	}

	private fun processMessagesFromUI() {
		var pushed = false
		for (message in ui.outgoing.receiveAll()) {
			pushed = true
			backend.ingoing.send(message)
			messagesFromUIDistributor.push(message)
		}

		if (pushed)
			backend.schedule()
	}

	private fun processMessagesFromDSP() {
		var pushed = false
		for (message in dsp.outgoing.receiveAll()) {
			pushed = true
			backend.ingoing.send(message)
			messagesFromDSPDistributor.push(message)
		}

		if (pushed)
			backend.schedule()
	}

	/**
	 * Send messages to Elastic from outside.
	 */
	@Synchronized
	fun send(message: ElasticMessage) {
		assertCorrectThread()

		when (message) {
			is CreateDefaultProjectMessage -> {
				runAction(CreateDefaultProject(message))
			}
		}

		backend.ingoing.send(message)

		synchronized(lock) {
			handleMessages = true
			lock.notifyAll()
		}
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
			override fun onMessage(message: ElasticMessage) {
				backend.ingoing.send(message)
				synchronized(lock) {
					handleMessages = true
					lock.notifyAll()
				}
			}

			override fun onUpdateSystem() = update(0)
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
			throw RuntimeException("ElasticSystem can only be used by the thread it was created in")
	}
}
