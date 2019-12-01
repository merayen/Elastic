package net.merayen.elastic.system

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

internal class LocksTest {
	@Test
	fun testLock() {
		data class MyLock(var name: String) : Locks.Lock()

		val locks = Locks()
		val peter = MyLock("Peter")
		locks.lock(peter)

		assertThrows<Locks.AlreadyLockedException> { locks.lock(MyLock("Peter")) }
		assertDoesNotThrow { locks.lock(MyLock("Gunnar")) }
	}
}