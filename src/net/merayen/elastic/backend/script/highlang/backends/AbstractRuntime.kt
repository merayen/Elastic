package net.merayen.elastic.backend.script.highlang.backends

import java.util.HashMap

abstract class AbstractRuntime {
	abstract fun run()
	abstract fun getRuntimeVariables(): HashMap<String, Any>
}