package net.merayen.elastic.system.intercom

class StatisticsReportMessage(
		val avgFrameTime: Double,
		val maxFrameTime: Double,
		val notProcessingFrameTimeAvg: Double,
		val nodeStats: Map<String, NodeStats>,
		val frameDuration: Double // In seconds
) : Message() {
	class NodeStats(
			val nodeClassPath: String,

			val minFrameTime: Double,
			val avgFrameTime: Double,
			val maxFrameTime: Double,

			/**
			 * How many times the node has been run in a single frame.
			 * Should usually be 1, but if it is put in a feedback-loop, it might have been several times.
			 */
			val cyclesInFrame: Int,

			/**
			 * How many processes this node has
			 */
			val processCount: Int
	)

	override fun dump(): MutableMap<String, Any> {
		TODO("not implemented")
	}
}