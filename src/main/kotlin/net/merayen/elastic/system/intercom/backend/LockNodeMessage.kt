package net.merayen.elastic.system.intercom.backend

import net.merayen.elastic.system.intercom.ElasticMessage

/**
 * Lock a resource in Elastic, meaning the one who locks it is the only one who can access it.
 * Not implemented yet.
 *
 * @param nodeId The group-node (poly_1, group_1 etc) that is to be locked
 */
class LockNodeMessage(val nodeId: String, val owner: String) : ElasticMessage