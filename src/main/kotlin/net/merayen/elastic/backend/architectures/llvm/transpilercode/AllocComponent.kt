package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Allocates and deallocates memory.
 *
 * TODO make it track allocations and deallocations when in debug mode
 */
class AllocComponent(private val log: LogComponent, private val debug: Boolean = false) {
	fun writeMalloc(codeWriter: CodeWriter, typeExpression: String, destinationVariable: String, sizeExpression: String) {
		with(codeWriter) {
			if (debug)
				log.write(codeWriter, "[AllocComponent] malloc(%lu)", "(unsigned long)$sizeExpression")

			Statement("$typeExpression $destinationVariable = malloc($sizeExpression)")
			If("$destinationVariable == NULL") {
				ohshit(codeWriter, "Could not allocate size=%lu", sizeExpression)
			}
		}
	}

	fun writeCalloc(codeWriter: CodeWriter, typeExpression: String, destinationVariable: String, countExpression: String, sizeExpression: String) {
		with(codeWriter) {
			if (debug)
				log.write(codeWriter, "[AllocComponent] calloc(%i, %lu)", "$countExpression, (unsigned long)$sizeExpression")

			Statement("$typeExpression $destinationVariable = calloc($countExpression, $sizeExpression)")
			If("$destinationVariable == NULL") {
				ohshit(codeWriter, "Could not allocate count=%i, size=%lu", "$countExpression, (unsigned long)$sizeExpression")
			}
		}
	}

	fun writeFree(codeWriter: CodeWriter, variable: String) {
		codeWriter.Call("free", variable)
	}
}