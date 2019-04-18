package net.merayen.elastic.backend.script.interpreter

import net.merayen.elastic.backend.script.parser.Parser
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AccessError(text: String) : InterpreterException(text)
class InvalidArrayVariable(arrayName: String) : AccessError(arrayName)
class OutOfBoundsArrayVariableAccess(arrayName: String, position: Int, size: Int) : AccessError("Tried to access array '$arrayName' on position $position which is outside 0 to $size")

open class IllegalArgument(text: String) : RuntimeException(text)

class IllegalArgumentType(index: Int, got: KClass<out InterpreterNode>, expected: KClass<out InterpreterNode>) : IllegalArgument(
        "Expected ${expected.simpleName} in argument position $index, but got ${got.simpleName}"
)

class IllegalArgumentCount(got: Int, minimum: Int, maximum: Int = minimum) : IllegalArgument(
        when {
            maximum == minimum && got != minimum -> "Got $got arguments but expected exactly $minimum arguments"
            got < minimum -> "Got $got arguments but expected at least $minimum arguments"
            got > maximum -> "Got $got arguments but can't take any more than $maximum arguments"
            else -> throw RuntimeException()
        }
)


class FunctionNotFound(functionName: String) : InterpreterException("$functionName was not found")


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
                @Suppress("UNCHECKED_CAST")
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


class Variable(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : InterpreterNode(env, parent, parserItem) {
    override fun eval() = env.variables[name]?.get(0) ?: 0f
}


/**
 * Set a value to a variable (or first item in an array)
 * Usage:
 *  let
 *      @my_variable_or_array
 *      1234  # Value, a variable or a function
 */
class Let(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 2) {
            throw IllegalArgumentCount(children.size, 2)
        }

        if (children[0] !is Variable) {
            throw IllegalArgumentType(0, children[0]::class, Variable::class)
        }
    }

    override fun eval(): Float {
        val array = env.variables[children[0].name]
        val value: Float = children[1].eval()

        if (array == null)
            throw InvalidArrayVariable(children[0].name)

        array[0] = value

        return value
    }
}


/**
 * Assigns a value to an array by index.
 * Usage:
 *  let-index
 *      @my_array   # Can also be a "variable", but a variable only has index 0 available
 *      123         # The index to place the value
 *      1.234       # The value. Can be a variable or a function
 */
class LetIndex(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    override fun eval(): Float {
        val array = env.variables[children[0].name]
        val index = children[1].eval().toInt()
        val value = children[2].eval()

        if (array == null)
            throw InvalidArrayVariable(children[0].name)

        if (index < 0 || index >= array.size)
            throw OutOfBoundsArrayVariableAccess(children[0].name, index, array.size)

        array[index] = value

        return value
    }
}


/**
 * Creates an array (or variable, which is just an array of size=1). It is not possible to set the size of the array dynamically. This is for performance reasons.
 * This is a no-op. The code will be scanned before execution to set up the float-buffers.
 * Usage:
 *  dim  # Creates an array (length 1 and up)
 *      @my_array
 *      100         # Optional. Length of array. If not given, it is 1. Must be more than 0
 *
 *  or
 *
 *  dim  # Creates a variable (actually an array with length 1)
 *      @my_variable
 */
class Dim(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    val arrayVariableName: String
    val arrayLength: Int

    init {
        if (children[0] !is Variable)
            throw IllegalArgumentType(0, children[0]::class, Variable::class)


        if (children.size == 1) {
            arrayLength = 1

        } else if (children.size == 2) {
            if (children[1] !is Constant)
                throw IllegalArgumentType(1, children[1]::class, Constant::class)

            arrayLength = children[1].eval().toInt()

            if (arrayLength < 1)
                throw IllegalArgument("Array length must be more than 0")

        } else {
            throw IllegalArgumentCount(children.size, 1, 2)

        }

        arrayVariableName = children[0].name
    }
}


/**
 * Get a floating point from an array.
 * Usage:
 *  get
 *      @an_array   # The array (or variable if index is 0) to retrieve float from
 *      123         # The index in the array. Can be a function or a variable too
 */
class Get(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 2)
            throw IllegalArgumentCount(children.size, 2)

        if (children[0] !is Variable)
            throw IllegalArgumentType(0, children[0]::class, Variable::class)
    }

    override fun eval(): Float {
        val arrayName = children[0].name
        val arrayIndex = children[1].eval().toInt()

        return env.variables[arrayName]?.get(arrayIndex) ?: throw InvalidArrayVariable(arrayName)
    }
}


/**
 * Adds two floats.
 * Usage:
 *  add
 *      1.123   # The value to add. Can be a variable or function too
 *      1.123   # Same as above
 *              # Optionally, add more arguments
 */
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
        var r = if (children.size > 0) children[0].eval() else 0f

        for (child in children.subList(1, children.size))
            r -= child.eval()

        return r
    }
}


class Increment(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 1)
            throw IllegalArgumentCount(children.size, 1)

        if (children[0] !is Variable)
            throw IllegalArgumentType(0, children[0]::class, Variable::class)
    }

    override fun eval(): Float {
        val array = env.variables[children[0].name] ?: throw InvalidArrayVariable(children[0].name)

        array[0]++

        return array[0]
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
            throw IllegalArgumentCount(children.size, 2)
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


class Ceil(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 1)
            throw IllegalArgumentCount(children.size, 1)
    }

    override fun eval() = ceil(children[0].eval())
}


class Floor(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 1)
            throw IllegalArgumentCount(children.size, 1)
    }

    override fun eval() = floor(children[0].eval())
}


class Round(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 1)
            throw IllegalArgumentCount(children.size, 1)
    }

    override fun eval() = round(children[0].eval())
}


class Min(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    override fun eval() = children.map { it.eval() }.min() ?: 0f
}


class Max(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    override fun eval() = children.map { it.eval() }.min() ?: 0f
}


class Mod(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 2)
            throw IllegalArgumentCount(children.size, 2)
    }

    override fun eval() = children[0].eval() % children[1].eval()
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


class Not(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) {
    init {
        if (children.size != 1)
            throw IllegalArgumentCount(children.size, 1)
    }

    override fun eval() = if (children[0].eval() > 0) 0f else 1f
}


class Any(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) {
    override fun eval() = if (children.any { it.eval() > 0f }) 1f else 0f
}

class All(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : ExecutionBlock(env, parent, parserItem) {
    override fun eval() = if (children.all { it.eval() > 0f }) 1f else 0f
}


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


class Equal(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    override fun eval(): Float {
        if (children.size == 0)
            return 1f

        val value = children[0].eval()

        for (child in children.subList(1, children.size))
            if (child.eval() != value)
                return 0f

        return 1f
    }
}

class MoreThan(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 2)
            throw IllegalArgumentCount(children.size, 2)
    }

    override fun eval() = if (children[0].eval() > children[1].eval()) 1f else 0f
}


class MoreThanOrEqual(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
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


class LessThanOrEqual(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    init {
        if (children.size != 2)
            throw IllegalArgumentCount(children.size, 2)
    }

    override fun eval() = if (children[0].eval() <= children[1].eval()) 1f else 0f
}


/**
 * No-op outside the interpreter.
 * Dumps the memory.
 * Usage:
 *  debug
 */
class Print(env: Environment, parent: InterpreterNode?, parserItem: Parser.Item) : FunctionType(env, parent, parserItem) {
    override fun eval(): Float {
        var result = 0f

        for (child in children) {
            result = child.eval()
            println("${child.name}: $result")
        }

        return result
    }
}


private val registry = mapOf<String, KClass<out InterpreterNode>>(
        "dim" to Dim::class,
        "let" to Let::class,
        "let-index" to LetIndex::class,
        "get" to Get::class,
        "increment" to Increment::class,
        "add" to Add::class,
        "subtract" to Subtract::class,
        "multiply" to Multiply::class,
        "pow" to Pow::class,
        "sin" to Sin::class,
        "ceil" to Ceil::class,
        "floor" to Floor::class,
        "round" to Round::class,
        "min" to Min::class,
        "max" to Max::class,
        "mod" to Mod::class,
        "if" to If::class,
        "then" to Then::class,
        "else" to Else::class,
        "not" to Not::class,
        "all" to All::class,
        "any" to Any::class,
        "while" to While::class,
        "do" to Do::class,
        "equal" to Equal::class,
        "more-than" to MoreThan::class,
        "less-than" to LessThan::class,
        "more-than-or-equal" to MoreThanOrEqual::class,
        "less-than-or-equal" to LessThanOrEqual::class,
        "print" to Print::class,
        "program" to Program::class
)

fun getInterpreterNode(env: Environment, parent: InterpreterNode?, item: Parser.Item): InterpreterNode {
    return when (item) {
        is Parser.Constant -> Constant(env, parent, item)
        is Parser.Variable -> Variable(env, parent, item)
        //is Parser.ArrayVariable -> ArrayVariableType(env, parent, item)
        is Parser.Function -> registry[item.name]?.primaryConstructor?.call(env, parent, item)
                ?: throw FunctionNotFound(item.name)
        else -> throw RuntimeException("Unknown Parser-item")
    }
}