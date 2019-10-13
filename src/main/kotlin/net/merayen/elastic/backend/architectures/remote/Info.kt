package net.merayen.elastic.backend.architectures.remote

import net.merayen.elastic.backend.architectures.IArchitecture

class Info : IArchitecture {
	override val description: String
		get() = "Running processor on an external device like a computer, Android etc, over TCP/IP. Not implemented yet"
	override val name: String
		get() = "Remote"
}
