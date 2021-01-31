package net.merayen.elastic.util

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

private fun log(level: LogLevel, text: String) {
	if (level.level < logLevel.level)
		return

	println("[${level.name}][${Thread.currentThread().stackTrace[3].className}]: $text")
}

fun logDebug(text: String) = log(LogLevel.DEBUG, text)
fun logInfo(text: String) = log(LogLevel.INFO, text)
fun logWarning(text: String) = log(LogLevel.WARNING, text)
fun logError(text: String) = log(LogLevel.ERROR, text)
fun logCritical(text: String) = log(LogLevel.CRITICAL, text)
