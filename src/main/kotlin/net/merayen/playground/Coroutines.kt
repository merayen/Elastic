package net.merayen.playground

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun doWorkB(): Int {
	println("Working B")
	delay(1000)
	return 1300;
}

suspend fun doWorkA(): Int {
	println("Working A")

	delay(1000L)
	return 1337;
}

suspend fun doIt(): Int {
	return doWorkA() + doWorkB()
}

fun main() = runBlocking {
	launch { doIt() }
	println("Worked.")
}