package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState

/**
 * @param uiobject The owner of this branch. Used for rebuilding the stack in EasyMotion.
 */
abstract class Branch(val uiobject: UIObject) {
	interface Handler {
		fun onEnter()
		fun onLeave()
	}

	class Control(private val onSelect: (key: KeyboardState.KeyStroke?) -> EasyMotionBranch?) {
		companion object {
			val STEP_BACK = object : EasyMotionBranch {
				override val easyMotionBranch = object : Branch(UIObject()) {}
			}
		}

		/**
		 * The EasyMotionBranch returned by onSelect-function on this Control.
		 * EasyMotion uses this variables to backtrace and rebuild its stack.
		 */
		var child: EasyMotionBranch? = null
			private set

		/**
		 * Called by EasyMotion when this Control gets selected (user typed its keys).
		 */
		fun select(keys: KeyboardState.KeyStroke?): EasyMotionBranch? {
			val result = onSelect(keys)

			child = if (result == Control.STEP_BACK)
				null
			else
				result

			return result
		}
	}

	var handler: Handler? = null

	val controls = HashMap<Set<KeyboardEvent.Keys>, Control>()

	/**
	 * Tells EasyMotion to focus here.
	 * EasyMotion will try to rebuild its stack from this element.
	 */
	fun focus() {
		val obj = uiobject.search.parentByInterface(EasyMotionMaster::class.java)
		if (obj != null) {
			obj.easyMotion.focus(this)
		}
	}
}