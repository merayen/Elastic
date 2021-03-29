package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.analyzer.node_dependency.toDependencyList
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.DSPModule
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.util.NetListMessages
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class LLVMDSPModule() : DSPModule() {
	private val transpiler: KClass<out Transpiler> = Transpiler::class
	private var currentTranspiler: Transpiler? = null
	private var upcomingNetList: NetList? = null
	private var currentNetList: NetList = NetList()

	private var llvmRunner: LLVMRunner? = null

	/**
	 * Messages queued that will be sent after the DSP backend (C program) has been started
	 */
	private val propertyMessages = ArrayList<NodePropertyMessage>()

	/**
	 * Only for debugging. Peek at generated C code.
	 */
	var listenCodeGen: ((code: String) -> Unit)? = null

	private fun handle(messages: Collection<ElasticMessage>) {
		for (message in messages) {
			when (message) {
				is NetListMessage -> { // These messages changes the structure of the netlist, so we will need to recompile
					if (upcomingNetList == null)
						upcomingNetList = currentNetList.copy()

					NetListMessages.apply(upcomingNetList!!, message)
				}
				is NodePropertyMessage -> {
					if (upcomingNetList == null) // If we are to recompile soon, we ignore the message as the new backend will have it
						sendProperty(message)
					else // We are replacing the netlist, so we need to queue the property-message
						propertyMessages.add(message) // TODO queue other message types too?
				}
				is ProcessRequestMessage -> {
					val upcomingNetList = upcomingNetList
					if (upcomingNetList != null) { // Begin compiling, but continue to use old backend
						startLLVMRunner(upcomingNetList)
						currentNetList = upcomingNetList
						this.upcomingNetList = null

						// Send queued NodePropertyMessages
						for (propertyMessage in propertyMessages)
							sendProperty(propertyMessage)
					} else if (llvmRunner == null) {
						val netList = NetList() // Temporary, mostly empty NetList
						val topNode = netList.createNode("temporary_top_node")
						val nodeProperties = NodeProperties(netList)
						nodeProperties.setName(topNode, "group")
						startLLVMRunner(netList)
					}

					process()
				}
			}
		}
	}

	private fun sendProperty(message: NodeMessage) = currentTranspiler!!.nodes[message.nodeId]!!.handle(message)

	private fun startLLVMRunner(netList: NetList) {
		currentTranspiler = null
		val tr = transpiler.primaryConstructor!!.call(netList, 44100, 16, 256, 4, 256, true) // ???
		val c = tr.transpile()
		currentTranspiler = tr

		listenCodeGen?.invoke(c)

		val llvm = LLVMBackend(c)
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

		outgoing.send(ProcessResponseMessage()) // TODO always send empty message? Send data individually as below?

		val doneResponse = llvmRunner.communicator.poll()

		// Expect "DONE" sent from DSP now
		if (doneResponse.capacity() != 4 || !"DONE".all { doneResponse.get() == it.toByte() })
			throw RuntimeException("Alignment error. Expected DONE as answer from backend")

		startTime += System.nanoTime()

		//println("DSP timings: Total=${startTime / 1000 / 1000.0}ms, DSP process=${startProcess / 1000 / 1000.0}ms")
		processing = false
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