package net.merayen.elastic.backend.logicnodes.list.frequency_1

import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * Asks the backend to send the UI spectrum data.
 * Need to send this at least once every second to keep streaming spectrum data from backend.
 */
class FrequencyRequestMessage(override val nodeId: String) : NodeDataMessage
