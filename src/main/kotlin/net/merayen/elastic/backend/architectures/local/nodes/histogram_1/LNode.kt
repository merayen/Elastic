package net.merayen.elastic.backend.architectures.local.nodes.histogram_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	private var resolution = 100
	private var start = -1f
	private var width = 2f
	private var buckets = FloatArray(0)

	/**
	 * How much each buckets should be reduced every second
	 */
	private var timeLastSent = 0L

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}

	override fun onProcess(data: InputFrameData?) {
		for (processor in processors) {
			processor as LProcessor
			processor.resolution = resolution
			processor.start = start
			processor.width = width
		}
	}

	override fun onParameter(instance: BaseNodeProperties?) {}

	override fun onFinishFrame() {
		if (resolution != buckets.size)
			buckets = FloatArray(resolution)

		// Sum all the buckets from the processors and push it into our sum buckets
		for (processor in processors) {
			processor as LProcessor
			val pbuckets = processor.buckets
			for (i in 0 until pbuckets.size)
				buckets[i] += pbuckets[i]
		}

		if (timeLastSent + 100 < System.currentTimeMillis()) {
			outgoing = Histogram1NodeOutputFrameData(nodeId = id, buckets = buckets.toTypedArray())

			// Reset buckets
			for (i in 0 until buckets.size)
				buckets[i] = 0f

			timeLastSent = System.currentTimeMillis()
		}
	}

	override fun onDestroy() {}
}