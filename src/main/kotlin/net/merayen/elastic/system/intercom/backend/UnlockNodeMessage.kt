package net.merayen.elastic.system.intercom.backend

import net.merayen.elastic.system.intercom.ElasticMessage

/**
 * Unlock a previously owned resource (node).
 */
class UnlockNodeMessage(val nodeId: String, val owner: String) : ElasticMessage