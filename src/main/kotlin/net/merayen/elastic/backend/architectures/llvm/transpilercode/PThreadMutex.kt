package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Hassle to write locking manually in C. Let's do it by zero-cost abstraction!
 */
class PThreadMutex(val name: String, log: LogComponent, debug: Boolean) : TranspilerComponent(log, debug) {
	fun writeDefinition(codeWriter: CodeWriter) {
		codeWriter.Statement("pthread_mutex_t $name")
	}

	fun writeInit(codeWriter: CodeWriter, variableExpression: String = "&$name") {
		with(codeWriter) {
			val resultVar = "${name}_init_result"
			Statement("int $resultVar")

			if (debug) {
				Statement("pthread_mutexattr_t mutex_attr")

				// Couldn't find documentation that this is needed...
				Call("memset", "&mutex_attr, 0, sizeof(pthread_mutexattr_t)")

				If("pthread_mutexattr_settype(&mutex_attr, PTHREAD_MUTEX_ERRORCHECK) != 0") {
					panic(codeWriter)
				}
				Statement("$resultVar = pthread_mutex_init($variableExpression, &mutex_attr)")
			} else {
				Statement("$resultVar = pthread_mutex_init($variableExpression, NULL)")
			}

			If("$resultVar == 0") {}
			ElseIf("$resultVar == EAGAIN") {
				panic(codeWriter, "$resultVar ended with EAGAIN")
			}
			ElseIf("$resultVar == ENOMEM") {
				panic(codeWriter, "$resultVar init: ENOMEM")
			}
			ElseIf("$resultVar == EPERM") {
				panic(codeWriter, "$resultVar init: EPERM")
			}
			Else {
				panic(codeWriter, "$resultVar init: (unknown error)")
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
	fun writeLock(
		codeWriter: CodeWriter,
		variableExpression: String = "&$name",
		block: () -> Unit
	) { // Should this really enclose? Returning inside will let the lock be locked...
		with(codeWriter) {
			Block {
				val resultVar = "${name}_lock_result"
				Statement("int $resultVar")
				Statement("$resultVar = pthread_mutex_lock($variableExpression)")
				If("$resultVar == 0") {
					block()
					If("pthread_mutex_unlock($variableExpression) != 0") {
						panic(codeWriter, "$name lock: Could not unlock")
					}
				}
				ElseIf("$resultVar == EINVAL") {
					panic(codeWriter, "$name lock: EINVAL")
				}
				ElseIf("$resultVar == EAGAIN") {
					panic(codeWriter, "$name lock: EAGAIN")
				}
				ElseIf("$resultVar == EDEADLK") {
					panic(codeWriter, "$name lock: EDEADLK")
				}
				Else {
					panic(codeWriter, "$name lock: (unknown error)")
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
	fun writeTryLock(
		codeWriter: CodeWriter,
		locked: () -> Unit,
		onBusy: (() -> Unit)? = null,
		variableExpression: String = "&$name"
	) {
		with(codeWriter) {
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
				panic(codeWriter, "$name trylock: EINVAL")
			}
			ElseIf("$resultVar == EAGAIN") {
				panic(codeWriter, "$name trylock: EAGAIN")
			}
		}
	}
}