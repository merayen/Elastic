package net.merayen.elastic.backend.logicnodes

import net.merayen.elastic.backend.data.project.Project
import net.merayen.elastic.backend.mix.Mixer
import net.merayen.elastic.backend.mix.Synchronization
import net.merayen.elastic.backend.nodes.LogicEnvironment
import net.merayen.elastic.backend.queue.Queue

/**
 * Environment that uses the current
 */
class Environment(mixer: Mixer, synchronization: Synchronization, sample_rate: Int, buffer_size: Int, project: Project)
    : LogicEnvironment(mixer, synchronization, sample_rate, buffer_size, project) {

    /**
     * The queue that should be used for any long-running process in the backend
     */
    val queue = Queue(4)
}
