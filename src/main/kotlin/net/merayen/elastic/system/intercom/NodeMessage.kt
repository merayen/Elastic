package net.merayen.elastic.system.intercom

interface NodeMessage : ElasticMessage {
	val nodeId: String
}
