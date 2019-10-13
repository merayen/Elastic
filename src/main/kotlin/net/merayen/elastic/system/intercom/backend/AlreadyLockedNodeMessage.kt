package net.merayen.elastic.system.intercom.backend

/**
 * Sent from Elastic when node could not be locked due to already being locked.
 *
 * @param nodeId ID of node trying to lock
 * @param currentOwner Current owner of the node
 */
class AlreadyLockedNodeMessage(val nodeId: String, val currentOwner: String) : ErrorMessage