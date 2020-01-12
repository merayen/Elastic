package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState
import java.util.*

/**
 * EasyMotion is a feature that is similar to vim's EasyMotion plugin (vim = command line text editor for g33ks).
 *
 * Q - Exit current object
 * &gt;ESC&LT; - Exit all controls
 */
class EasyMotion(uiObject: UIObject, private val initialControl: Control) {
	class AlreadyInStackException : RuntimeException()
	class EasyMotionSetupError(message: String) : RuntimeException(message)

	interface Handler {
		/**
		 * When user types a letter that does not exist in the current level.
		 */
		fun onMistype(keyStroke: KeyboardState.KeyStroke)

		/**
		 * User goes into EasyMotion-mode.
		 */
		fun onEnter()

		/**
		 * Called when user leaves EasyMotion (pushing Q enough times or pushing ESC)
		 */
		fun onLeave()
	}

	init {
		if (uiObject !is EasyMotionMaster)
			throw EasyMotionSetupError("Object EasyMotion is put on need to implement EasyMotionMaster")
	}

	var handler: Handler? = null

	private val keyboardState = KeyboardState()

	/**
	 * "Call-stack" of the EasyMotion controllers.
	 * Every time user goes into a Control, we add it to the stack.
	 */
	private val stack = ArrayDeque<Control>()

	init {
		keyboardState.handler = object : KeyboardState.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke) {
				when {
					keyStroke.equalsKeys(setOf(KeyboardEvent.Keys.Q)) -> leave()
					keyStroke.equalsKeys(setOf(KeyboardEvent.Keys.ESCAPE)) -> leaveAll()
					else -> enterControl(keyStroke)
				}
			}
		}
	}

	/**
	 * Pass all keyboard events to us. We will send them to the correct EasyMotion Control for processing.
	 */
	fun handle(event: KeyboardEvent) {
		keyboardState.handle(event)
	}

	fun setHintMode(active: Boolean) {
		TODO("Implement hint-mode")
	}

	/**
	 * Begins EasyMotion navigation.
	 * Uses the uiObject's Control
	 */
	private fun enterControl(keyStroke: KeyboardState.KeyStroke) {
		val children = if (stack.isEmpty()) // Handle when EasyMotion is not active. We boot from initialControl
			listOf(initialControl)
		else // EasyMotion already active. We try to dive deeper into the tree
			stack.peekLast().children

		var hit: Control? = null
		for (child in children) {
			if (keyStroke.equalsKeys(child.trigger)) {
				if (hit != null) {
					println("WARNING: EasyMotion: Conflicting KeyStroke for control $hit and $child")
				} else {
					hit = child
				}
			}
		}

		if (hit != null) {
			if (stack.isEmpty())
				handler?.onEnter()

			select(hit)

			// If the Control has sub-items, we will stay at it. Otherwise, we jump out of it.
			// A control without subitems is typically a terminating action that does something.
			if (hit.children.isEmpty())
				leave()

		} else {
			handler?.onMistype(keyStroke)
		}
	}

	/**
	 * Leave the current control, if any.
	 */
	private fun leave() {
		if (stack.isEmpty())
			return

		stack.removeLast().onUnselect()

		if (!stack.isEmpty())
			stack.peekLast().onSelect()
		else
			handler?.onLeave()
	}

	fun leaveAll() {
		if (stack.isEmpty())
			return

		for (obj in stack)
			obj.onUnselect()

		stack.clear()
		handler?.onLeave()
	}

	private fun select(control: Control) {
		if (control in stack)
			throw AlreadyInStackException()

		stack.add(control)

		control.onSelect()
	}
}