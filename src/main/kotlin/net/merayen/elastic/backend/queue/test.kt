package net.merayen.elastic.backend.queue

fun no(message: String = "") {
    throw RuntimeException(message)
}

fun test() {
    val queue = Queue(4)

    class TestData(
            @Volatile var task1Finished: Boolean = false,
            @Volatile var task2And3ConcurrencyResultFinished: Int = 0,
            @Volatile var task2Counter: Int = 0
    )

    val testData = TestData()

    queue.addTask(object : QueueTask() {
        override fun onCleanup() {

        }

        override fun onProcess() {
            testData.task1Finished = true
        }

        override fun onCancel() = no()
    })

    Thread.sleep(100) // Should be enough time to process. May fail is computer is really slow

    if (!testData.task1Finished)
        no()

    // Task 2
    val someSequence = Any()
    val resultArray = IntArray(100)

    for (u in 0 until resultArray.size) {
        queue.addTask(object : QueueTask(someSequence) {
            override fun onCleanup() {
                if (u == 90)
                    println("Rensker $u")
            }

            override fun onProcess() {
                //Thread.sleep(resultArray.size.toLong() - i)
                Thread.sleep(500)
                synchronized(testData.task2Counter) {
                    resultArray[u] = testData.task2Counter
                    println("Kjører $u ${testData.task2Counter}")
                    testData.task2Counter++
                }
            }

            override fun onCancel() {}
        })
    }

    //queue.join()

    Thread.sleep(10000)

    for (i in 0 until resultArray.size)
        if (resultArray[i] != i)
            no("i should be $i but is ${resultArray[i]}")
}