package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class TransportStartPlaybackMessage(override val nodeId: String) : NodeDataMessage  // TODO move out of group_1 as it is used by poly_1 too