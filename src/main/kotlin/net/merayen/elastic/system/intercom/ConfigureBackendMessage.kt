package net.merayen.elastic.system.intercom

class ConfigureBackendMessage(
	val sampleRate: Int,
	val frameSize: Int,
	val depth: Int,
) : ElasticMessage