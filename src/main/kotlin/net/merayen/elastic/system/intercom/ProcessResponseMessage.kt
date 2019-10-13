package net.merayen.elastic.system.intercom

import java.util.*

/**
 * Message is sent to backend when we want to process a new frame.
 * Backend responds with a new ProcessMessage with the processed data.
 * TODO split this into DoProcessMessage, FinishedProcessMessage
 */
class ProcessResponseMessage : ElasticMessage {
	/**
	 * Resulting data from the processing backend
	 */
	var output: Map<String, OutputFrameData> = HashMap()

	/**
	 * Returned by the backend in intervals, to report statistics for UI
	 */
	var statisticsReportMessage: StatisticsReportMessage? = null
}
