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
	private var easyMotion: EasyMotion? = null
		private set
		get() {
			if (field == null) {
				val obj = uiobject.search.parentByInterface(EasyMotionMaster::class.java)
					?: throw RuntimeException("EasyMotion not found")

				field = obj.easyMotion
			}

			return field
		}

	val parent: EasyMotionControllable?
		get() = uiobject.search.parentByInterface(EasyMotionControllable::class.java)

	/**
	 * When user types a key to choose this Control
	 */
	abstract fun onSelect(keyStroke: KeyboardState.KeyStroke)

	/**
	 * When user leaves this Control and enter its parent.
	 * Use it e.g close a dialog etc.
	 */
	abstract fun onLeave()

	/**
	 * Define the keystroke that selects this Control.
	 * Leave the Set empty to capture all keys that are not captured by other Controls.
	 */
	abstract val trigger: Set<KeyboardEvent.Keys>

	private val keyboardState = KeyboardState()

	fun handle(key: KeyboardEvent) {
		keyboardState.handle(key)
	}

	/**
	 * Make this the active EasyMotion control.
	 */
	fun select() {
		val easyMotion = easyMotion ?: throw RuntimeException("This Control is not in the tree below EasyMotion")

		easyMotion.select(this)
	}

	/**
	 * Retrieves all controls available under this
	 */
	fun getChildren(): List<Control> {
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

	companion object {
		fun create(uiobject: UIObject, keys: Set<KeyboardEvent.Keys>, func: (keyStroke: KeyboardState.KeyStroke) -> Unit): UIObject = object : UIObject(), EasyMotionControllable {
			override val easyMotionControl = object : Control(uiobject) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) = func(keyStroke)
				override fun onLeave() {}
				override val trigger = keys
			}
		}
	}
}