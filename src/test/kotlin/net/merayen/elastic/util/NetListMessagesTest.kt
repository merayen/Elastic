package net.merayen.elastic.util

import net.merayen.elastic.backend.architectures.local.JavaLocalDSPBackend
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.netlist.Node
import org.junit.jupiter.api.Test

internal class NetListMessagesTest {
	@Test
	fun testProperties() {

	}

	fun test() {
		val netlist = NetList()

		val test1 = createTestNode(netlist)
		val test2 = createTestNode(netlist)

		netlist.connect(test1, "output", test2, "input")

		val dspBackend = JavaLocalDSPBackend()

		for (m in NetListMessages.disassemble(netlist))
			dspBackend.ingoing.send(m)

		val t = System.currentTimeMillis() + 3000
		while (t > System.currentTimeMillis()) { // Let it run for 3 seconds
			try {
				Thread.sleep(10)
			} catch (e: InterruptedException) {
				throw RuntimeException(e)
			}
		}

		dspBackend.stop()
	}

	private fun createTestNode(netlist: NetList): Node {
		val n = netlist.createNode()
		n.properties.put("name", "test")
		n.properties.put("version", 100)

		n.properties.put("test", "Hello on you!")
		netlist.createPort(n, "input")
		netlist.createPort(n, "output")
		return n
	}
}