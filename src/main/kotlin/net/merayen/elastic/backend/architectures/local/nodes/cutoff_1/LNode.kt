package net.merayen.elastic.backend.architectures.local.nodes.cutoff_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	override fun onInit() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onSpawnProcessor(lp: LocalProcessor?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onProcess(data: InputFrameData?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onParameter(instance: BaseNodeProperties?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onFinishFrame() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onDestroy() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}