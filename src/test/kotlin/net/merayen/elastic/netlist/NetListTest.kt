package net.merayen.elastic.netlist

import org.json.simple.JSONObject
import org.json.simple.parser.ParseException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class NetListTest {
	@Test
	fun testSerialization() {
		val netlist = create()

		val first_dump = Serializer.dump(netlist).toJSONString()
		val second_dump = Serializer.dump(Serializer.restore(parse(first_dump))).toJSONString()

		assertEquals(first_dump, second_dump)
	}

	companion object {
		fun create(): NetList {
			val netlist = NetList()
			val midi_in = netlist.createNode()
			netlist.createPort(midi_in, "output")
			midi_in.properties["some midi setting"] = "hokay"

			val gen = netlist.createNode()
			netlist.createPort(gen, "frequency")
			gen.properties["some gen setting"] = 1337
			netlist.connect(midi_in, "output", gen, "frequency")

			val gen2 = netlist.createNode()
			netlist.createPort(gen2, "frequency")
			netlist.connect(gen2, "frequency", midi_in, "output")

			assertSame(netlist.getNode(midi_in.id), midi_in)

			assertThrows(NetList.NodeNotFound::class.java) { netlist.remove(Node("fjklsehdfjks")) }

			assertNotNull(netlist.getPort(gen, "frequency"))

			assertThrows(NetList.PortNotFound::class.java) { netlist.connect(midi_in, "output", gen, "port_does_not_exist") }


			// Try connecting with non-existent port
			assertThrows(NetList.PortNotFound::class.java) { netlist.connect(midi_in, "port_does_not_exist", gen, "frequency") }

			// Try to connect over an existing connection
			assertThrows(NetList.AlreadyConnected::class.java) { netlist.connect(midi_in, "output", gen, "frequency") }

			// Try to connect opposite way. Should fail
			assertThrows(NetList.AlreadyConnected::class.java) { netlist.connect(gen, "frequency", midi_in, "output") }

			val lines = netlist.getConnections(midi_in, "output")
			assertEquals(2, lines.size)

			assertTrue(netlist.isConnected(midi_in, "output", gen, "frequency"))

			return netlist
		}
	}

	private fun parse(dump: String): JSONObject {
		try {
			return org.json.simple.parser.JSONParser().parse(dump) as JSONObject
		} catch (e: ParseException) {
			throw RuntimeException(e)
		}

	}
}