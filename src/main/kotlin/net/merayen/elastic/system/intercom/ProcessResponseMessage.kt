package net.merayen.elastic.system.intercom

import java.util.*

/**
 * Message is sent to backend when we want to process a new frame.
 * Backend responds with a new ProcessMessage with the processed data.
 */
class ProcessResponseMessage : ElasticMessage { // TODO tissue:llvm remove, will send individual messages using NodeDataMessage instead
	/**
	 * Resulting data from the processing backend
	 *
	 * TODO remove, nodes will send data individually instead
	 */
	var output: Map<String, OutputFrameData> = HashMap()

	/**
	 * Returned by the backend in intervals, to report statistics for UI
	 */
	var statisticsReportMessage: StatisticsReportMessage? = null
}
