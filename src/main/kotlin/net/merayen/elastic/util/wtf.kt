package net.merayen.elastic.util

fun wtf(): Nothing = throw RuntimeException("should not happen")
fun wtf(message: String): Nothing = throw RuntimeException("should not happen: $message")