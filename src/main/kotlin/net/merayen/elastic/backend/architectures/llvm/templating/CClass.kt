package net.merayen.elastic.backend.architectures.llvm.templating

import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

/**
 * Wannabe class in C.
 * Template classes.
 */
abstract class CClass(val name: String) {
	interface Method {
		fun write(): CodeWriter
	}

	fun addInstanceMethod(codeWriter: CodeWriter, returnType: String, name: String, args: String = "", block: () -> Unit) {
		codeWriter.Method(returnType, "${this.name}_$name", "struct ${this@CClass.name}* this${if (args.isNotEmpty()) ", $args" else ""}") {
			block()
		}
	}

	fun addInstanceMethodHeader(codeWriter: CodeWriter, returnType: String, name: String, args: String = "") {
		codeWriter.Method(returnType, "${this.name}_$name", "struct ${this@CClass.name}* this${if (args.isNotEmpty()) ", $args" else ""}")
	}

	protected open fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {}

	protected open fun onWriteDestroy(codeWriter: CodeWriter, allocComponent: AllocComponent?) {}

	protected open fun onWriteMethodHeaders(codeWriter: CodeWriter) {}

	/**
	 * Create instance methods in this method using addInstanceMethod()
	 */
	protected abstract fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?)

	/**
	 * Return the members on this class.
	 */
	protected abstract fun onWriteMembers(codeWriter: CodeWriter)

	fun writeCallDestroy(variable: String) = "${name}_destroy($variable)"

	fun writeInstanceType() = "struct $name"

	/**
	 * Write the method name of a... method.
	 */
	fun writeMethodName(method: String) = "${name}_$method"

	fun writeCall(codeWriter: CodeWriter, method: String, args: String = "") {
		codeWriter.Call("${name}_$method", args)
	}

	fun writeStruct(codeWriter: CodeWriter, instances: List<String> = listOf()) {
		codeWriter.Struct(name, instances) {
			onWriteMembers(codeWriter)
		}
	}

	fun writeHeaders(codeWriter: CodeWriter) {
		onWriteMethodHeaders(codeWriter)
	}

	fun writeMethods(codeWriter: CodeWriter, allocComponent: AllocComponent? = null) {
		codeWriter.Method("struct $name*", "${name}_create", "") {
			if (allocComponent != null)
				allocComponent.writeCalloc(codeWriter, "struct $name*", "this", "1", "sizeof(struct $name)")
			else
				codeWriter.Statement("struct $name* this = calloc(1, sizeof(struct $name))")
			onWriteInit(codeWriter, allocComponent)
			codeWriter.Return("this")
		}

		// Only use this method if memory has already been allocated as create() automatically initializes!
		codeWriter.Method("void", "${name}_init", "struct $name* this") {
			onWriteInit(codeWriter, allocComponent)
		}

		codeWriter.Method("void", "${name}_destroy", "struct $name* this") {
			onWriteDestroy(codeWriter, allocComponent)
			if (allocComponent != null)
				allocComponent.writeFree(codeWriter, "this")
			else
				codeWriter.Call("free", "this")
		}

		// Add the node's own class instance method at the end
		onWriteMethods(codeWriter, allocComponent)
	}
}
