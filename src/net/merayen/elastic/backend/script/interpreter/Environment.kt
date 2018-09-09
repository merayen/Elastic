package net.merayen.elastic.backend.script.interpreter

import net.merayen.elastic.backend.script.parser.Parser

class Environment(val variableCountRestriction: Int = 256, val arrayHeapSizeRestriction: Int = 1024) {
	val variables = HashMap<String,Float>()
	val arrayVariables = HashMap<String,FloatArray>()
}