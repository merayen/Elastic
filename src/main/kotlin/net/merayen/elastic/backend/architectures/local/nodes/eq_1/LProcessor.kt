package net.merayen.elastic.backend.architectures.local.nodes.eq_1

import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.util.Postmaster

class LProcessor : LocalProcessor() {
	override fun onInit() {}
	override fun onPrepare() {}
	override fun onProcess() {}
	override fun onMessage(message: ElasticMessage) {}
	override fun onDestroy() {}
}