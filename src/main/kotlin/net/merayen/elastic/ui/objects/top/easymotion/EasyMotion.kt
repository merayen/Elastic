package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.top.Window
import net.merayen.elastic.ui.util.KeyboardState
import java.util.*

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

				current.easyMotionBranch.handler?.onEnter()

				val result = control.select()

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

				return
			}
		}

		handler?.onMistype(keyStroke)
	}

	/**
	 * Leave the current control, if any.
	 */
	private fun leave(branch: EasyMotionBranch) {
		if (branch !in stack)
			throw RuntimeException("${branch} is not in the EasyMotion stack")

		val index = stack.indexOf(branch)

		var removing = false

		stack.removeIf {
			if (it == branch)
				removing = true;

			removing
		}
	}

	fun getCurrentStack() = ArrayList(stack)

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
			val isAChildOfUs = it.easyMotionBranch.uiobject.topMost === initialBranch.easyMotionBranch.uiobject.topMost

			if (isAChildOfUs && isRemoving) // This is just a sanity check
				throw RuntimeException("Should not happen that a child is attached to the UIObject-tree while its parent is not")

			if (!isAChildOfUs)
				isRemoving = true

			!isAChildOfUs
		}

		printDebug()
	}

	private fun printDebug() {
		(initialBranch.easyMotionBranch.uiobject as Window).debug.set("EasyMotion", stack.map { (it as? UIObject)?.javaClass?.simpleName })
	}
}