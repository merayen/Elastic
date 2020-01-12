package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState
import java.util.*

/**
 * EasyMotion control.
 * A control is an object that is bound to a key.
 *
 * @param uiobject The object that is
 */
abstract class Control(val uiobject: UIObject) {
	protected val easyMotion = uiobject.search.parentByInterface(EasyMotionMaster::class.java)

	/**
	 * When user types a key to choose this Control or comes back from child Control
	 */
	abstract fun onSelect()

	/**
	 * When user leaves this Control by going upward.
	 */
	abstract fun onUnselect()

	/** // TODO DELETE?
	 * When user enters a child of this Control.
	 * @param control The Control being entered
	 */
	abstract fun onEnter(control: Control)

	/**
	 * Define the keystroke that selects this Control.
	 */
	abstract val trigger: Set<KeyboardEvent.Keys>

	val keyboardState = KeyboardState()

	fun handle(key: KeyboardEvent) {
		keyboardState.handle(key)
	}

	/**
	 * Retrieves all controls available under this
	 */
	val children: List<Control>
		get() {
			val result = ArrayList<Control>()

			val stack = ArrayDeque<UIObject>()
			stack.addAll(uiobject.children)

			while (!stack.isEmpty()) {
				val child = stack.pop()

				if (child is EasyMotionControllable)
					result.add(child.easyMotionControl)
				else
					stack.addAll(child.children)
			}

			return result
		}
}