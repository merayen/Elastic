package net.merayen.elastic.backend.analyzer

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.util.NetListMessages
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class NetListUtilTest {
	@Test
	fun `getInputPortFormat detects connected ports output format`() {
		val netlist = NetList()
		listOf(
			CreateNodeMessage("a", "a", null),
			CreateNodePortMessage("a", "in"),
			CreateNodePortMessage("a", "out", Format.AUDIO),

			CreateNodeMessage("b", "b", null),
			CreateNodePortMessage("b", "in"),

			NodeConnectMessage("a", "out", "b", "in"),
		).forEach { NetListMessages.apply(netlist, it) }

		val netListUtil = NetListUtil(netlist)

		assertEquals(Format.AUDIO, netListUtil.getInputPortFormat(netlist.nodes.first { it.id == "b" }, "in"))
		assertNull(netListUtil.getInputPortFormat(netlist.nodes.first { it.id == "a"}, "in"))
		assertNull(netListUtil.getInputPortFormat(netlist.nodes.first { it.id == "a"}, "out"))
	}
}