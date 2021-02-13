package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

class PThreadCond(val name: String, val log: LogComponent? = null) {
	fun writeDefinition(codeWriter: CodeWriter) {
		codeWriter.Statement("pthread_cond_t $name")
	}

	fun writeInit(codeWriter: CodeWriter, variableExpression: String = "&$name") {
		with(codeWriter) {
			val resultVar = "${name}_init_result"
			Statement("int $resultVar")
			Statement("$resultVar = pthread_cond_init($variableExpression, NULL)")
			If("$resultVar == 0") {}
			ElseIf("$resultVar == EAGAIN") {
				ohshit(codeWriter, "$resultVar init: EAGAIN")
			}
			ElseIf("$resultVar == ENOMEM") {
				ohshit(codeWriter, "$resultVar init: ENOMEM")
			}
			Else {
				ohshit(codeWriter, "$resultVar init: (unknown error)")
			}
		}
	}

	/**
	 * Waits for someone to wake us up or
	 */
	fun writeTimedWait(
		codeWriter: CodeWriter,
		mutex: PThreadMutex,
		seconds: Double,
		condVariableExpression: String = "&$name",
		mutexVariableExpression: String = "&${mutex.name}",
		onTimeOut: (() -> Unit)? = null
	) {
		with(codeWriter) {
			val resultVar = "${name}_result"
			Statement("int $resultVar")
			Block {
				Statement("struct timespec ${name}_wait_until")
				Call("clock_gettime", "CLOCK_REALTIME, &${name}_wait_until")
				Statement("${name}_wait_until.tv_sec += ${seconds.toInt()}")
				Statement("${name}_wait_until.tv_nsec += ${((seconds % 1) * 1E9).toInt()}")
				Statement("${name}_wait_until.tv_sec += ${name}_wait_until.tv_nsec / 1000000000")
				Statement("${name}_wait_until.tv_nsec %= 1000000000")
				//log?.write(codeWriter, "$defaultName timeout: %ld , %ld", "${defaultName}_wait_until.tv_sec, ${defaultName}_wait_until.tv_nsec")
				Statement("${name}_result = pthread_cond_timedwait($condVariableExpression, ${mutexVariableExpression}, &${name}_wait_until)") // Wait for someone to wake us up, also temporary unlocks the mutex
			}
			If("${name}_result == EINVAL") {
				ohshit(codeWriter, "$name timedwait: EINVAL")
			}
			ElseIf("${name}_result == EPERM") {
				ohshit(codeWriter, "$name timedwait: EPERM")
			}
			if (onTimeOut != null) {
				ElseIf("${name}_result == ETIMEDOUT") {
					onTimeOut()
				}
			}
		}
	}

	fun writeWait(
		codeWriter: CodeWriter,
		mutex: PThreadMutex,
		condVariableExpression: String = "&$name",
		mutexVariableExpression: String = "&${mutex.name}",
	) {
		with (codeWriter) {
			Call("pthread_cond_wait", "$condVariableExpression, $mutexVariableExpression") // Wait for someone to wake us up, also temporary unlocks the mutex
		}
	}

	fun writeCallBroadcast(codeWriter: CodeWriter, variableExpression: String = "&$name") {
		with(codeWriter) {
			val resultVar = "${name}_broadcast_result"
			Statement("int $resultVar")
			Statement("$resultVar = pthread_cond_broadcast($variableExpression)")
			If("$resultVar != 0") {
				ohshit(codeWriter, "$name broadcast: %i", resultVar)
			}
		}
	}
}