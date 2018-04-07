package net.merayen.elastic.ui.controller

import org.json.simple.JSONObject

import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster.Message

class NetListController(gate: Gate) : Controller(gate) {
	override fun onInit() {}
	override fun onMessageFromBackend(message: Message) {}
	override fun onMessageFromUI(message: Message) {}
}
