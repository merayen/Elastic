package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.analyzer.node_dependency.toDependencyList
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.DSPModule
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.util.AverageStat
import net.merayen.elastic.util.NetListMessages
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class LLVMDSPModule : DSPModule() {
	var debug: Boolean = false
	private val transpiler: KClass<out Transpiler> = Transpiler::class
	private var currentTranspiler: Transpiler? = null
	private var upcomingNetList: NetList? = null
	private var currentNetList: NetList = NetList()

	private var llvmRunner: LLVMRunner? = null

	private val processDuration = AverageStat<Double>(1000)

	/**
	 * Messages queued that will be sent after the DSP backend (C program) has been started
	 */
	private val queuedMessages = ArrayList<NodeMessage>()

	/**
	 * Only for debugging. Peek at generated C code.
	 */
	var listenCodeGen: ((code: String) -> Unit)? = null

	private fun handle(messages: Collection<ElasticMessage>) {
		for (message in messages) {
			//println("LLVM: Got message $message")
			when (message) {
				is NetListMessage -> { // These messages changes the structure of the netlist, so we will need to recompile
					if (upcomingNetList == null)
						upcomingNetList = currentNetList.copy()

					NetListMessages.apply(upcomingNetList!!, message)
				}
				is NodePropertyMessage -> {
					queueMessage(message)
				}
				is NodeDataMessage -> {
					queueMessage(message)
				}
				is ProcessRequestMessage -> {
					val upcomingNetList = upcomingNetList
					if (upcomingNetList != null) { // Begin compiling, but continue to use old backend
						val t = System.currentTimeMillis()
						LLVMNetList.process(upcomingNetList) // Prepare the NetList
						startLLVMRunner(upcomingNetList)
						currentNetList = upcomingNetList
						this.upcomingNetList = null

						// Send messages that got queued when we were rebuilding the netlist
						for (x in queuedMessages)
							sendNodeMessage(x)

						println("LLVM: recompiling took ${System.currentTimeMillis() - t}ms")

					} else if (llvmRunner == null) {
						val netList = NetList() // Temporary, mostly empty NetList
						val topNode = netList.createNode("temporary_top_node")
						val nodeProperties = NodeProperties(netList)
						nodeProperties.setName(topNode, "group")
						startLLVMRunner(netList)
					}

					process()
				}
				is BackendReadyMessage -> {} // I don't think we need to handle this one...? Or?
				else -> error("Not sure how to handle message '$message'")
			}
		}
	}

	private fun queueMessage(message: NodeMessage) {
		if (upcomingNetList == null) // If we are to recompile soon, we ignore the message as the new backend will have it
			sendNodeMessage(message)
		else // We are replacing the netlist, so we need to queue the property-message
			queuedMessages.add(message)
	}

	private fun sendNodeMessage(message: NodeMessage) = currentTranspiler!!.nodes[message.nodeId]!!.handle(message)

	private fun startLLVMRunner(netList: NetList) {
		currentTranspiler = null
		val tr = transpiler.primaryConstructor!!.call(netList, 44100, 16, 256, 4, 256, debug) // ???
		val c = tr.transpile()
		currentTranspiler = tr

		listenCodeGen?.invoke(c)

		val llvm = LLVMBackend(c, false) // TODO make debug mode toggle able
		val llvmCommunicator = LLVMCommunicator(llvm)
		llvmCommunicator.send("PING".toByteArray())

		val noe = llvmCommunicator.poll()

		if (
			noe.get().toChar() != 'P' || noe.get().toChar() != 'O' || noe.get().toChar() != 'N' || noe.get()
				.toChar() != 'G'
		)
			throw RuntimeException("Expected PONG on PING request")

		llvmRunner = LLVMRunner(netList, llvm, llvmCommunicator)
	}

	private var processing = false
	private var lastDurationPrint = 0L
	private fun process() {
		if (processing) error("Should not happen")
		processing = true
		val llvmRunner = llvmRunner ?: throw RuntimeException("Can not process. There is no DSP process running")

		val currentTranspiler = currentTranspiler!!

		var startTime = -System.nanoTime()

		// Prepare all nodes for processing, by walking
		val dependencyList = toDependencyList(currentNetList)
		for ((node, _) in dependencyList.walk())
			currentTranspiler.nodes[node]!!.prepareFrame()

		for (node in currentTranspiler.nodes.values)
			for (buffer in node.retrieveDataToDSP())
				llvmRunner.communicator.send(buffer.array())

		// Make the DSP backend work on the frame
		var startProcess = -System.nanoTime()
		llvmRunner.communicator.send("PROCESS".toByteArray())

		// Then wait for incoming data that we will receive when nodes are getting finished to process (happens while processing frame)
		val response = llvmRunner.communicator.poll()

		startProcess += System.nanoTime()

		response.rewind()
		if (response.capacity() != 8 && !"NODEDATA".all { response.get() == it.toByte() })
			throw RuntimeException("Expected to receive NODEDATA")

		// Read response from all nodes
		for (node in currentTranspiler.nodes.toSortedMap().values) {
			val nodeData = llvmRunner.communicator.poll()

			if (nodeData.capacity() == 4 && "DONE".all { response.get() == it.toByte() })
				throw RuntimeException("Retrieval of NODEDATA from DSP offset error")

			nodeData.rewind()

			outgoing.send(node.onDataFromDSP(nodeData))
		}

		val doneResponse = llvmRunner.communicator.poll()

		// Expect "DONE" sent from DSP now
		if (doneResponse.capacity() != 4 || !"DONE".all { doneResponse.get() == it.toByte() })
			throw RuntimeException("Alignment error. Expected DONE as answer from backend")

		startTime += System.nanoTime()

		processDuration.add(startTime / 1000000.0)

		//println("LLVM DSP timings: Total=${startTime / 1000 / 1000.0}ms, DSP process=${startProcess / 1000 / 1000.0}ms")

		if (lastDurationPrint < System.currentTimeMillis()) {
			println("LLVM DSP process: ${processDuration.info()} (ms)")
			lastDurationPrint = System.currentTimeMillis() + 1000
		}
		processing = false

		outgoing.send(ProcessResponseMessage()) // TODO always send empty message? Send data individually as below?
		notifyElasticSystem()
	}

	override fun onInit() {}

	override fun onUpdate() {
		handle(ingoing.receiveAll())
	}

	override fun onEnd() {
		val runContext = llvmRunner
		if (runContext != null) {
			runContext.end()
			llvmRunner = null
		}
	}
}