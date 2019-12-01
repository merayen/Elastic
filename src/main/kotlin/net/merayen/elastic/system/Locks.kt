package net.merayen.elastic.system

/**
 * Common class to register all locks in Elastic.
 * Should contain locks for nodes, perhaps parameters and more.
 */
class Locks {
	class AlreadyLockedException(lock: Lock) : RuntimeException(lock.toString())

	abstract class Lock

	private val locks = ArrayList<Lock>()

	@Synchronized
	fun lock(lock: Lock) {
		for (l in locks)
			if (lock == l)
				throw AlreadyLockedException(l)

		locks.add(lock)
	}

	fun unlock(lock: Lock) {
		locks.remove(lock)
	}
}