package net.merayen.elastic.backend.context

import net.merayen.elastic.backend.data.project.Project
import net.merayen.elastic.backend.logicnodes.Environment
import net.merayen.elastic.backend.mix.Mixer
import net.merayen.elastic.backend.mix.Synchronization
import net.merayen.elastic.system.ElasticSystem
import net.merayen.elastic.system.intercom.ProcessMessage
import net.merayen.elastic.system.intercom.backend.InitBackendMessage

internal object Env {
	fun create(system: ElasticSystem, message: InitBackendMessage): Environment {
		val mixer = Mixer()
		mixer.reconfigure(message.sample_rate, 2, message.depth)

		val sync = Synchronization(mixer, message.sample_rate, message.buffer_size, object : Synchronization.Handler {
			override fun needData() {
				system.sendMessageToBackend(listOf(ProcessMessage()))
			}

			override fun behind() {}
		})

		return Environment(mixer, sync, message.sample_rate, message.buffer_size, Project(message.project_path))
	}
}
