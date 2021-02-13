package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Hassle to write locking manually in C. Let's do it by zero-cost abstraction!
 */
class PThreadMutex(val name: String, val log: LogComponent? = null, val debug: Boolean = false) {
	fun writeDefinition(codeWriter: CodeWriter) {
		codeWriter.Statement("pthread_mutex_t $name")
	}

	fun writeInit(codeWriter: CodeWriter, variableExpression: String = "&$name") {
		with(codeWriter) {
			val resultVar = "${name}_init_result"
			Statement("int $resultVar")
			Statement("$resultVar = pthread_mutex_init($variableExpression, NULL)")
			If("$resultVar == 0") {}
			ElseIf("$resultVar == EAGAIN") {
				ohshit(codeWriter, "$resultVar ended with EAGAIN", debug = debug)
			}
			ElseIf("$resultVar == ENOMEM") {
				ohshit(codeWriter, "$resultVar init: ENOMEM", debug = debug)
			}
			ElseIf("$resultVar == EPERM") {
				ohshit(codeWriter, "$resultVar init: EPERM", debug = debug)
			}
			Else {
				ohshit(codeWriter, "$resultVar init: (unknown error)", debug = debug)
			}
		}
	}

	/**
	 * Write lock construct.
	 *
	 * Code must not contain any break/continue/return-statement that breaks out of this block.
	 *
	 * @param block Code to run if lock was acquired. DO NOT return; or continue;/break; out of the clause. Lock will not be released!
	 */
	fun writeLock(codeWriter: CodeWriter, variableExpression: String = "&$name", block: () -> Unit) { // Should this really enclose? Returning inside will let the lock be locked...
		with(codeWriter) {
			Block {
				val resultVar = "${name}_lock_result"
				Statement("int $resultVar")
				Statement("$resultVar = pthread_mutex_lock($variableExpression)")
				If("$resultVar == 0") {
					block()
					Call("pthread_mutex_unlock", variableExpression)
				}
				ElseIf("$resultVar == EINVAL") {
					ohshit(codeWriter, "$name lock: EINVAL")
				}
				ElseIf("$resultVar == EAGAIN") {
					ohshit(codeWriter, "$name lock: EAGAIN")
				}
				ElseIf("$resultVar == EDEADLK") {
					ohshit(codeWriter, "$name lock: EDEADLK")
				}
				Else {
					ohshit(codeWriter, "$name lock: (unknown error)", debug = debug)
				}
			}
		}
	}

	/**
	 * Try to lock a resource. Execute success if successful, otherwise failure.
	 *
	 * NOTE: Will not unlock if escaping the closure with e.g return or break
	 *
	 * @param locked The code that should run with the lock. DO NOT RETURN, BREAK OR CONTINUE, block need to complete to unlock lock
	 */
	fun writeTryLock(codeWriter: CodeWriter, locked: () -> Unit, onBusy: (() -> Unit)? = null, variableExpression: String = "&$name") {
		with (codeWriter) {
			val resultVar = "${name}_trylock_result"
			Statement("int $resultVar")
			Statement("$resultVar = pthread_mutex_trylock(&$variableExpression)")
			If("$resultVar == 0") {
				locked()
				Statement("pthread_mutex_unlock(&$variableExpression)")
			}
			if (onBusy != null) {
				ElseIf("$resultVar == EBUSY") {
					onBusy()
				}
			}
			ElseIf("$resultVar == EINVAL") {
				ohshit(codeWriter, "$name trylock: EINVAL")
			}
			ElseIf("$resultVar == EAGAIN") {
				ohshit(codeWriter, "$name trylock: EAGAIN")
			}
		}
	}
}