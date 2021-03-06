package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.top.window.Window
import net.merayen.elastic.ui.util.KeyboardState
import java.util.*
import kotlin.collections.ArrayList

/**
 * EasyMotion is a feature that is similar to vim's EasyMotion plugin (vim = command line text editor for g33ks).
 *
 * Q - Exit current object
 * &gt;ESC&LT; - Exit all controls
 */
class EasyMotion(private val initialBranch: EasyMotionBranch) {
	class AlreadyInStackException : RuntimeException()
	class EasyMotionSetupError(message: String) : RuntimeException(message)

	interface Handler {
		/**
		 * When user types a letter that does not exist in the current level.
		 */
		fun onMistype(keyStroke: KeyboardState.KeyStroke)

		fun onEnter(branch: EasyMotionBranch)

		fun onLeave(branch: EasyMotionBranch)
	}

	private val keyboardState = KeyboardState()

	var handler: Handler? = null

	/**
	 * "Call-stack" of the EasyMotion controllers.
	 * Every time user goes into a Control, we add it to the stack.
	 */
	private val stack = ArrayDeque<EasyMotionBranch>()

	init {
		if (initialBranch !is UIObject)
			throw RuntimeException("initialBranch must be an UIObject")

		keyboardState.handler = object : KeyboardState.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke) {
				enterControl(keyStroke)
			}
		}

		stack.add(initialBranch)
	}

	/**
	 * Pass all keyboard events to us. We will send them to the correct EasyMotion Control for processing.
	 */
	fun handle(event: KeyboardEvent) = keyboardState.handle(event)

	/**
	 * Begins EasyMotion navigation.
	 * Uses the uiObject's Control
	 */
	private fun enterControl(keyStroke: KeyboardState.KeyStroke) {
		val current = stack.last

		for ((key, control) in current.easyMotionBranch.controls) {
			if (keyStroke.equalsKeys(key)) {
				enterControl(control, keyStroke)
				return
			}
		}

		// Capture keys for control that takes every other key not defined by the other Controls, if any present
		val everything = current.easyMotionBranch.controls[setOf()]
		if (everything != null) {
			enterControl(everything, keyStroke)
			return
		}

		handler?.onMistype(keyStroke)
	}

	private fun enterControl(control: Branch.Control, keyStroke: KeyboardState.KeyStroke) {
		val current = stack.last

		current.easyMotionBranch.handler?.onEnter()

		val result = control.select(keyStroke)

		if (result == Branch.Control.STEP_BACK) {
			if (stack.size > 1) {
				current.easyMotionBranch.handler?.onLeave()
				handler?.onLeave(current)
				stack.removeLast()
			}
		} else {
			val child = control.child

			if (child != null && child.easyMotionBranch.controls.isNotEmpty()) {
				handler?.onEnter(child)
				if (child in stack)
					throw AlreadyInStackException()

				stack.add(child)
			}
		}

	}

	/**
	 * Leave the current control, if any.
	 */
	private fun leave(branch: EasyMotionBranch) {
		if (branch !in stack)
			throw RuntimeException("$branch is not in the EasyMotion stack")

		val index = stack.indexOf(branch)

		var removing = false

		stack.removeIf {
			if (it == branch)
				removing = true

			removing
		}
	}


	fun getCurrentStack(): Collection<EasyMotionBranch> = Collections.unmodifiableCollection(stack)

	/**
	 * Rebuilds the tree by checking which Controls are still attached.
	 *
	 * Should be called for every frame.
	 *
	 * If user closes a window with e.g a mouse, that UIObject representing that window will get detached. We will
	 * detect that and jump upwards automatically in our stack.
	 */
	fun update() {
		var isRemoving = false
		stack.removeIf {
			val isAChildOfUs = (it as UIObject).topMost === (initialBranch as UIObject).topMost

			if (isAChildOfUs && isRemoving) // This is just a sanity check
				throw RuntimeException("Should not happen that a child is attached to the UIObject-tree while its parent is not")

			if (!isAChildOfUs)
				isRemoving = true

			!isAChildOfUs
		}

		printDebug()
	}

	fun focus(branch: EasyMotionBranch) {
		// Tries to rebuild the stack by walking from branch in reverse
		if (branch is UIObject) {
			val stack = ArrayList<EasyMotionBranch>()

			var current = branch as UIObject
			var i = 0
			while (i++ < 1000) {  // We walk backwards
				if (current is EasyMotionBranch)
					stack.add(current as EasyMotionBranch)

				current = current.parent ?: break
			}

			if (i == 1000)
				throw RuntimeException("Should not happen")

			stack.reversed()

			if (stack.last() !== initialBranch) {
				println("EasyMotion ERROR: Could not find initialBranch on top")
				return
			}

			this.stack.clear()
			this.stack.addAll(stack.reversed())
		} else {
			println("EasyMotion ERROR: Could not focus element $branch")
		}
	}

	/**
	 * Returns true if object is directly focused.
	 */
	fun isFocused(branch: EasyMotionBranch) = stack.last === branch

	/**
	 * Returns true if object is directly focused or one of its childs are.
	 */
	fun isActive(branch: EasyMotionBranch) = branch in stack

	private fun printDebug() {
		(initialBranch as Window).debug.set("EasyMotion", stack.map { (it as? UIObject)?.javaClass?.simpleName })
	}
}