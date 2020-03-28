package net.merayen.elastic.util

import kotlin.math.max

enum class LogLevel(val level: Int) {
	DEBUG(0),
	INFO(1),
	WARNING(2),
	ERROR(3),  // Something really wrong
	CRITICAL(4);  // Elastic quits
}

/**
 * Change the log level to show more or less logging
 */
var logLevel = LogLevel.DEBUG

private fun log(what: Any, level: LogLevel, text: String) {
	if (level.level < logLevel.level)
		return

	val packageList = what.javaClass.packageName.split("/")
	val name = packageList.subList(max(0, packageList.size - 3), packageList.size).joinToString()

	println("[${level.name}][$name]: $text")
}

fun logDebug(what: Any, text: String) = log(what, LogLevel.DEBUG, text)
fun logInfo(what: Any, text: String) = log(what, LogLevel.INFO, text)
fun logWarning(what: Any, text: String) = log(what, LogLevel.WARNING, text)
fun logError(what: Any, text: String) = log(what, LogLevel.ERROR, text)
fun logCritical(what: Any, text: String) = log(what, LogLevel.CRITICAL, text)
