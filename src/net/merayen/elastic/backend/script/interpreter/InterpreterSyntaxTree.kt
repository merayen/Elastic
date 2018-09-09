package net.merayen.elastic.backend.script.interpreter

import net.merayen.elastic.backend.script.parser.Parser
import kotlin.math.sin
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AccessError(text: String) : InterpreterException(text)
class InvalidArrayVariable(arrayName: String) : AccessError(arrayName)
class OutOfBoundsArrayVariableAccess(arrayName: String, position: Int, size: Int) : AccessError("Tried to access array '$arrayName' on position $position which is outside 0 to $size")

open class IllegalArgument(text: String) : RuntimeException(text)

class IllegalArgumentType(index: Int, got: KClass<out InterpreterNode>, expected: KClass<out InterpreterNode>) : IllegalArgument(
		"Expected ${expected.simpleName} in argument position ${index}, but got ${got.simpleName}"
)

class IllegalArgumentCount(got: Int, minimum: Int, maximum: Int = minimum) : IllegalArgument(
		when {
			maximum == minimum && got != minimum -> "Got $got arguments but expected exactly $minimum arguments"
			got < minimum -> "Got $got arguments but expected at least $minimum arguments"
			got > maximum -> "Got $got arguments but can't take any more than $maximum arguments"
			else -> throw RuntimeException()
		}
)


class FunctionNotFound(functionName: String) : InterpreterException("${functionName} was not found")


abstract class InterpreterNode(val env: Environment, val parent: InterpreterNode?, parserItem: Parser.Item) {
	val children = ArrayList<InterpreterNode>() // Our own list of children, as we build our tree to be the same as Parser's tree under execution (or validation)
	val name = parserItem.name

	init {
		for (child in parserItem.children) {
			children.add(getInterpreterNode(env, this, child))
		}
	}

	open fun eval() = 0f

	fun <T : InterpreterNode> find(type: KClass<out T>): ArrayList<T> {
		val r = ArrayList<T>()

		for (child in children) {
			if (type.isInstance(child))
				r.add(child as T)

			r.addAll(child.find(type))
		}

		return r
	}
}


/**
 * Anything that can run.
 */
abstract class FunctionType(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : InterpreterNode(env, parent, parserItem)


/**
 * Anything that contains arguments that can be run.
 */
abstract class ExecutionBlock(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	override fun eval(): Float {
		var r = 0f
		for (child in children)
			r = child.eval()

		return r // Returns last result
	}
}


/**
 * The very top-most node in the interpreter tree. Does nothing else than being the root for everything.
 */
class Program(env: Environment, parent: InterpreterNode?, parseItem: Parser.Item) : ExecutionBlock(env, parent, parseItem)


class Constant(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : InterpreterNode(env, parent, parserItem) {
	val constantValue = name.toFloat()

	override fun eval() = constantValue
}


class VariableType(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : InterpreterNode(env, parent, parserItem) {
	override fun eval() = env.variables[name] ?: 0f
}


class ArrayVariableType(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : InterpreterNode(env, parent, parserItem)



class Let(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	val isArray: Boolean

	init {
		if (children.size == 3) {
			if (children[0] !is ArrayVariableType) {
				throw IllegalArgumentType(0, children[0]::class, ArrayVariableType::class)
			}
			isArray = true

		} else if (children.size == 2) {
			if (children[0] !is VariableType) {
				throw IllegalArgumentType(0, children[0]::class, VariableType::class)
			}
			isArray = false

		} else {
			throw IllegalArgumentCount(children.size, 2, 3)
		}
	}

	override fun eval(): Float {
		if (isArray) {
			val array = env.arrayVariables[children[0].name]
			val index = children[1].eval().toInt()
			val value = children[2].eval()

			if (array == null)
				throw InvalidArrayVariable(children[0].name)

			if (index < 0 || index >= array.size)
				throw OutOfBoundsArrayVariableAccess(children[0].name, index, array.size)

			array[index] = value

		} else {
			env.variables[children[0].name] = children[1].eval()
		}

		return 0f
	}
}


/**
 * Creates an array. It is not possible to set the size of the array dynamically. This is for performance reasons.
 * This does nothing. The code will be scanned before execution to set up the float-buffers.
 */
class Dim(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	val arrayVariableName: String
	val arrayLength: Float

	init {
		if (children.size != 2)
			throw IllegalArgumentCount(children.size, 2)

		if (children[0] !is ArrayVariableType)
			throw IllegalArgumentType(0, children[0]::class, ArrayVariableType::class)

		if (children[1] !is Constant)
			throw IllegalArgumentType(1, children[1]::class, Constant::class)

		arrayLength = children[1].eval()

		if(arrayLength < 1)
			throw IllegalArgument("Array length must be more than 0")

		arrayVariableName = children[0].name
	}
}


/**
 * Get a floating point from an array.
 */
class Get(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if(children.size != 2)
			throw IllegalArgumentCount(children.size, 2)

		if(children[0] !is ArrayVariableType)
			throw IllegalArgumentType(0, children[0]::class, ArrayVariableType::class)
	}

	override fun eval(): Float {
		val arrayName = children[0].name
		val arrayIndex = children[1].eval().toInt()
		val array = env.arrayVariables[arrayName]

		if(array == null)
			throw InvalidArrayVariable(arrayName)

		return array[arrayIndex]
	}
}

class Add(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	override fun eval(): Float {
		var r = 0f

		for (child in children)
			r += child.eval()

		return r
	}
}


class Subtract(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	override fun eval(): Float {
		var r = if(children.size > 0) children[0].eval() else 0f

		for (child in children.subList(1, children.size))
			r -= child.eval()

		return r
	}
}


class Increment(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	override fun eval(): Float {
		var r = 0f

		for (child in children.subList(1, children.size))
			r += child.eval()

		env.variables[children[0].name] = (env.variables[children[0].name] ?: 0f) + r

		return r
	}
}


class Multiply(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	override fun eval(): Float {
		var r = 1f

		for (child in children)
			r *= child.eval()

		return r
	}
}


class Pow(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init { // Validates the arguments (children)
		if (children.size != 2)
			throw IllegalArgumentCount(children.size, 2);
	}

	override fun eval(): Float {
		return Math.pow(
				children[0].eval().toDouble(),
				children[1].eval().toDouble()
		).toFloat()
	}
}


class Sin(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if (children.size != 1)
			throw IllegalArgumentCount(children.size, 1)
	}

	override fun eval() = sin(children[0].eval())
}


class If(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if (children.size < 2 || children.size > 3)
			throw IllegalArgumentCount(children.size, 2, 3)
	}

	override fun eval(): Float {
		val r = children[0].eval()

		return if (children.size == 2) {
			if (r > 0) children[1].eval()
			else 0f

		} else if (children.size == 3) {
			if (r > 0) children[1].eval()
			else children[2].eval()

		} else {
			throw RuntimeException()
		}
	}
}


class Then(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) // TODO assert correct parent
class Else(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) // TODO assert correct parent


class While(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if (children.size != 2)
			throw IllegalArgumentCount(children.size, 2)

		if (children[0] !is FunctionType)
			throw IllegalArgumentType(0, children[0]::class, FunctionType::class)

		if (children[1] !is Do)
			throw IllegalArgumentType(1, children[1]::class, Do::class)
	}

	override fun eval(): Float {
		var r = 0f
		while (true) {
			if (children[0].eval() <= 0)
				break

			r = children[1].eval()
		}

		return r
	}
}


class Do(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) // TODO assert correct parent


class MoreThan(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if (children.size != 2)
			throw IllegalArgumentCount(children.size, 2)
	}

	override fun eval() = if (children[0].eval() > children[1].eval()) 1f else 0f
}


class LessThan(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
	init {
		if (children.size != 2)
			throw IllegalArgumentCount(children.size, 2)
	}

	override fun eval() = if (children[0].eval() < children[1].eval()) 1f else 0f
}

private val registry = mapOf<String, KClass<out InterpreterNode>>(
		"dim" to Dim::class,
		"let" to Let::class,
		"get" to Get::class,
		"increment" to Increment::class,
		"add" to Add::class,
		"subtract" to Subtract::class,
		"multiply" to Multiply::class,
		"pow" to Pow::class,
		"sin" to Sin::class,
		"if" to If::class,
		"then" to Then::class,
		"else" to Else::class,
		"while" to While::class,
		"do" to Do::class,
		"more-than" to MoreThan::class,
		"less-than" to LessThan::class,
		"program" to Program::class
)

fun getInterpreterNode(env: Environment, parent: InterpreterNode?, item: Parser.Item): InterpreterNode {
	return when (item) {
		is Parser.Constant -> Constant(env, parent, item)
		is Parser.Variable -> VariableType(env, parent, item)
		is Parser.ArrayVariable -> ArrayVariableType(env, parent, item)
		is Parser.Function -> registry[item.name]?.primaryConstructor?.call(env, parent, item) ?: throw FunctionNotFound(item.name)
		else -> throw RuntimeException("Unknown Parser-item")
	}
}