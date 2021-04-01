package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Allocates and deallocates memory.
 *
 * TODO make it track allocations and deallocations when in debug mode
 */
class AllocComponent(log: LogComponent, debug: Boolean = false) : TranspilerComponent(log, debug) {
	fun writeMalloc(codeWriter: CodeWriter, typeExpression: String, destinationVariable: String, sizeExpression: String) {
			codeWriter.Member(typeExpression, "$destinationVariable = NULL")
			writeMalloc(codeWriter, destinationVariable, sizeExpression)
	}

	fun writeMalloc(codeWriter: CodeWriter, destinationVariable: String, sizeExpression: String) {
		with(codeWriter) {
			If("$sizeExpression <= 0") {
				panic(codeWriter, "Could not allocate size=%lu", "(unsigned long)($sizeExpression)")
			}

			writeLog(codeWriter, "[AllocComponent] malloc(%lu)", "(unsigned long)($sizeExpression)")

			Statement("$destinationVariable = malloc($sizeExpression)")
		}
	}

	fun writeCalloc(
		codeWriter: CodeWriter,
		typeExpression: String,
		destinationVariable: String,
		countExpression: String,
		sizeExpression: String
	) {
		with(codeWriter) {
			writeLog(codeWriter, "[AllocComponent] calloc(%i, %lu)", "$countExpression, (unsigned long)$sizeExpression")

			Statement("$typeExpression $destinationVariable = calloc($countExpression, $sizeExpression)")
			If("$destinationVariable == NULL") {
				panic(codeWriter, "Could not allocate count=%i, size=%lu", "$countExpression, (unsigned long)$sizeExpression")
			}
		}
	}

	fun writeFree(codeWriter: CodeWriter, variable: String) {
		codeWriter.Call("free", variable)
	}
}