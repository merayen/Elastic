package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState

/**
 * EasyMotion control.
 * A control is an object that is bound to a key.
 */
abstract class Control(private val uiobject: UIObject) {
	/**
	 * When user types a key to choose this Control.
	 */
	abstract fun onSelect()

	/**
	 * Called when action is executed
	 */
	abstract fun onAction(key: KeyboardEvent.Keys)

	abstract val triggers: Array<KeyboardEvent.Keys>

	val keyboardState = KeyboardState()

	fun handle(key: KeyboardEvent) {
		keyboardState.handle(key)
	}
}