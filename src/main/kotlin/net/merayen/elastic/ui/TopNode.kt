package net.merayen.elastic.ui

import net.merayen.elastic.system.intercom.ElasticMessage

interface TopNode {
	fun retrieveMessagesFromUI(): Collection<ElasticMessage>
	fun sendMessagesToUI(messages: Collection<ElasticMessage>)
}