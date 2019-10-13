package net.merayen.elastic.ui

import net.merayen.elastic.system.intercom.ElasticMessage

interface TopNode {
	fun retrieveMessagesFromUI(): Collection<ElasticMessage>
	fun sendMessageToUI(message: ElasticMessage)
}