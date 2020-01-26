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
		 * TODO Should we have this, or should those be catched by ANY-Controls?
		 */
		fun onMistype(keyStroke: KeyboardState.KeyStroke)
	}

	private val keyboardState = KeyboardState()

	init {
		if (uiObject !is EasyMotionMaster)
			throw EasyMotionSetupError("Object EasyMotion is put on need to implement EasyMotionMaster")

		initialControl.getChildren()

		keyboardState.handler = object : KeyboardState.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke) {
				enterControl(keyStroke)
			}
		}
	}

	var handler: Handler? = null

	/**
	 * "Call-stack" of the EasyMotion controllers.
	 * Every time user goes into a Control, we add it to the stack.
	 */
	private val stack = ArrayDeque<Control>()

	/**
	 * Pass all keyboard events to us. We will send them to the correct EasyMotion Control for processing.
	 */
	fun handle(event: KeyboardEvent) {
		keyboardState.handle(event)
	}

	/**
	 * Begins EasyMotion navigation.
	 * Uses the uiObject's Control
	 */
	private fun enterControl(keyStroke: KeyboardState.KeyStroke) {
		val children = if (stack.isEmpty()) // Handle when EasyMotion is not active. We boot from initialControl
			listOf(initialControl)
		else // EasyMotion already active. We try to dive deeper into the tree
			stack.peekLast().getChildren()

		// Check for duplicates
		val checked = HashSet<Set<KeyboardEvent.Keys>>()
		for (child in children) {
			if (child.trigger in checked) {
				println("WARNING: EasyMotion: Conflicting KeyStroke for keystroke ${child.trigger}")
				return
			}
			checked.add(child.trigger)
		}

		var hit: Control? = null
		for (child in children) {
			if (child.trigger in checked)

				if (keyStroke.equalsKeys(child.trigger))
					hit = child
		}

		if (hit == null) {  // See if there are any ANY-receivers that takes all the keys that are not set
			val control = children.filter { it.trigger.isEmpty() }
			if (control.isNotEmpty())
				hit = control[0]
		}

		if (hit != null) {
			try {
				enter(hit, keyStroke)
			} finally {
				// If the Control has sub-items, we will stay at it. Otherwise, we jump out of it.
				// A control without sub-items is typically a terminating action that does something.
				if (hit.getChildren().isEmpty())
					leave()
			}

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

		stack.removeLast()
	}

	/**
	 * Base on this control.
	 * EasyMotion rebuilds the whole stack based on this Control, meaning the user will from now on do commands from
	 * this branch.
	 *
	 * Does not call onSelect() on Control, as this function is not thought to activate UI features.
	 */
	fun select(control: Control) {
		if (control.uiobject.topMost !== initialControl.uiobject.topMost)
			throw RuntimeException("Control is not connected to the UIObject tree where EasyMotionMaster is attached")

		val stack = ArrayList<Control>()
		stack.add(control)

		while (stack.last().parent != null)
			stack.add(stack.last().parent!!.easyMotionControl)

		if (stack.first().getChildren().isEmpty())
			stack.removeAt(0)

		this.stack.clear()
		this.stack.addAll(stack.reversed())
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
			val isAChildOfUs = it.uiobject.topMost === initialControl.uiobject.topMost

			if (isAChildOfUs && isRemoving) // This is just a sanity check
				throw RuntimeException("Should not happen that a child is attached to the UIObject-tree while its parent is not")

			if (!isAChildOfUs)
				isRemoving = true

			!isAChildOfUs
		}
	}

	private fun enter(control: Control, keyStroke: KeyboardState.KeyStroke) {
		if (control in stack)
			throw AlreadyInStackException()

		stack.add(control)

		control.onSelect(keyStroke)
	}
}