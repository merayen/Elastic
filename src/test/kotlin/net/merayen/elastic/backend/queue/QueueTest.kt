package net.merayen.elastic.backend.queue

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class QueueTest {
	@Test
	@Disabled("Needs more work in general")
	fun order() {
		val queue = Queue(4)

		class TestData(
			@Volatile var task1Finished: Boolean = false,
			@Volatile var task2And3ConcurrencyResultFinished: Int = 0,
			@Volatile var task2Counter: Int = 0
		)

		val testData = TestData()

		queue.addTask(object : QueueTask() {
			override fun onCleanup() {}

			override fun onProcess() {
				testData.task1Finished = true
			}

			override fun onCancel() = throw RuntimeException("Should not call onCancel()")
		})

		Thread.sleep(100) // Should be enough time to process. May fail is computer is really slow

		assertTrue(testData.task1Finished)

		// Task 2
		val someSequence = Any()
		val resultArray = IntArray(100)

		for (u in resultArray.indices) {
			queue.addTask(object : QueueTask(someSequence) {
				override fun onCleanup() {
					if (u == 90)
						;//println("Rensker $u")
				}

				override fun onProcess() {
					//Thread.sleep(resultArray.size.toLong() - i)
					Thread.sleep(10)
					synchronized(testData.task2Counter) {
						resultArray[u] = testData.task2Counter
						//println("Kjører $u ${testData.task2Counter}")
						testData.task2Counter++
					}
				}

				override fun onCancel() {}
			})
		}

		queue.join()

		assertEquals((0 until 100).toList(), resultArray.toList())
	}
}