package net.merayen.elastic.backend.logicnodes.list.metronome_1

import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * @param current The current beat. 0 = start at bar, 1 = beat number 1 after bar
 */
class MetronomeBeatMessage(override val nodeId: String, val current: Int, val division: Int) : NodeDataMessage