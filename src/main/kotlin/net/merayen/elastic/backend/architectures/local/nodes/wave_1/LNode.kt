package net.merayen.elastic.backend.architectures.local.nodes.wave_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.wave_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData
import kotlin.math.PI
import kotlin.math.sin

class LNode : LocalNode(LProcessor::class.java) {
	companion object {
		val noise = FloatArray(256)
		val sine = FloatArray(256)
		val triangle = FloatArray(256)
		val saw = FloatArray(256)
		val square = FloatArray(10)

		init {
			for (i in 0 until noise.size)
				noise[i] = (Math.random() * 2 - 1).toFloat()

			for (i in 0 until sine.size)
				sine[i] = sin((i / sine.size.toDouble()) * PI * 2).toFloat() * 2 - 1

			for (i in 0 until saw.size)
				saw[i] = (i / saw.size.toFloat()) * 2 - 1

			for (i in 0 until square.size)
				square[i] = if (i < square.size / 2) -1f else 1f
		}
	}

	private var type: Data.Type? = null

	override fun onInit() {}

	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: InputFrameData?) {
		for (processor in processors) {
			processor as LProcessor
			processor.type = type
		}
	}

	override fun onParameter(instance: BaseNodeData?) {
		instance as Data
		val type = instance.type

		if (type != null)
			this.type = Data.Type.valueOf(type)
	}

	override fun onFinishFrame() {}
	override fun onDestroy() {}
}