package net.merayen.elastic.util

import net.merayen.elastic.backend.architectures.Architecture
import net.merayen.elastic.backend.architectures.Dispatch
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.backend.InitBackendMessage
import org.junit.jupiter.api.Assertions.*
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

		val dispatch = Dispatch(Architecture.LOCAL) { message ->

		}

		dispatch.launch(InitBackendMessage(44100, 16, 1024, ""))

		for (m in NetListMessages.disassemble(netlist))
			dispatch.executeMessage(m)

		val t = System.currentTimeMillis() + 3000
		while (t > System.currentTimeMillis()) { // Let it run for 3 seconds
			try {
				Thread.sleep(10)
			} catch (e: InterruptedException) {
				throw RuntimeException(e)
			}

		}

		dispatch.stop()
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