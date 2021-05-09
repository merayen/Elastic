package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Tests that the nodes gets processed in the correct order and that they are not repeatedly processed.
 */
internal class SupervisorNodeTest : LLVMNodeTest() {
	@Test
	fun `process nodes only once per frame`() {
		/**
		 * Test node that ensures that it is processed only once per frame
		 */
		class TestNode(nodeId: String) : TranspilerNode(nodeId) {
			override val nodeClass = object : NodeClass() {
				override fun onWriteParameters(codeWriter: CodeWriter) {
					codeWriter.Member("bool", "processed")
				}

				override fun onWritePrepare(codeWriter: CodeWriter) {
					codeWriter.Statement("this->parameters.processed = false")
				}

				override fun onWriteProcess(codeWriter: CodeWriter) {
					with(codeWriter) {
						If("this->parameters.processed") {
							writePanic(codeWriter, "Processed more than once in a single frame. Something is wrong")
						}
						Statement("this->parameters.processed = true")
					}
				}
			}
		}

		val supervisor = createSupervisor(
			true, mapOf(
				"test" to TestNode::class,
				"group" to Group::class,
			)
		)

		supervisor.ingoing.send(
			listOf(
				CreateNodeMessage("a", "test", "top"),
				CreateNodePortMessage("a", "out", Format.SIGNAL),

				CreateNodeMessage("b", "test", "top"),
				CreateNodePortMessage("b", "out", Format.SIGNAL),

				CreateNodeMessage("c", "test", "top"),
				CreateNodePortMessage("c", "in_a"),
				CreateNodePortMessage("c", "in_b"),

				NodeConnectMessage("a", "out", "c", "in_a"),
				NodeConnectMessage("b", "out", "c", "in_b"),
			)
		)

		for (i in 0 until 2) {
			supervisor.ingoing.send(ProcessRequestMessage())
			supervisor.onUpdate()
			assertTrue(supervisor.outgoing.receiveAll().any { it is ProcessResponseMessage })
		}

		supervisor.onEnd()

		// If we get here without the LLVM backend crashing, also, the node over panicing (see writePanic), we are good!
	}

	/**
	 * Check that the methods on the node gets called in the correct order.
	 */
	@Test
	fun `call order`() {
		/**
		 * Test node that ensures that it is processed only once per frame
		 */
		class TestNode(nodeId: String) : TranspilerNode(nodeId) {
			override val nodeClass = object : NodeClass() {
				override fun onWriteParameters(codeWriter: CodeWriter) {
					codeWriter.Member("int", "state")
				}

				override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
					super.onWriteInit(codeWriter, allocComponent)
					codeWriter.Statement("this->parameters.state = 1")
				}

				override fun onWritePrepare(codeWriter: CodeWriter) {
					with(codeWriter) {
						If("this->parameters.state != 1") {
							writePanic(codeWriter, "Expected onWriteInit code to be run before onWritePrepare")
						}
						codeWriter.Statement("this->parameters.state = 2")
					}
				}

				override fun onWriteProcess(codeWriter: CodeWriter) {
					with(codeWriter) {
						If("this->parameters.state != 2") {
							writePanic(codeWriter, "Expected onWritePrepare code to be run before onWriteProcess")
						}
						Statement("this->parameters.state = 3")
					}
				}

				override fun onWriteDataSender(codeWriter: CodeWriter) {
					with(codeWriter) {
						If("this->parameters.state != 3") {
							writePanic(codeWriter, "Expected onWriteProcess code to be run before onWriteDataSender")
						}
						Statement("this->parameters.state = 1") // Reset state, getting ready for next frame
						Call("send", "0, NULL")
					}
				}
			}
		}

		val supervisor = createSupervisor(
			true, mapOf(
				"test" to TestNode::class,
				"group" to Group::class,
			)
		)

		supervisor.ingoing.send(CreateNodeMessage("a", "test", "top"))
		supervisor.onUpdate()

		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		assertTrue(supervisor.outgoing.receiveAll().any { it is ProcessResponseMessage })

		supervisor.onEnd()
	}
}