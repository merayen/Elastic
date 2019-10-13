package net.merayen.elastic.system.intercom

/**
 * BackendModule sends this message when it has restored and sent out all messages for recreating the project.
 *
 * When other ElasticModules receive this, they should begin their processing.
 */
class BackendReadyMessage : ElasticMessage