package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent

/**
 * @param uiobject The owner of this branch. Used for rebuilding the stack in EasyMotion.
 */
abstract class Branch(val uiobject: UIObject) {
	interface Handler {
		fun onEnter()
		fun onLeave()
	}

	class Control(private val onSelect: () -> EasyMotionBranch?) {
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
		fun select(): EasyMotionBranch? {
			val result = onSelect()

			child = if (result == Control.STEP_BACK)
				null
			else
				result

			return result
		}
	}

	var handler: Handler? = null

	val controls = HashMap<Set<KeyboardEvent.Keys>, Control>()
}