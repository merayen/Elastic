package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState

/**
 * @param uiobject The owner of this branch. Used for rebuilding the stack in EasyMotion.
 */
abstract class Branch(val uiobject: UIObject, val outline: UIObject = uiobject) {
	interface Handler {
		fun onEnter()
		fun onLeave()
	}

	val inFocus: Boolean
		get() = getEasyMotionMaster()?.isFocused(uiobject as EasyMotionBranch) ?: false

	val isActive: Boolean
		get() = getEasyMotionMaster()?.isActive(uiobject as EasyMotionBranch) ?: false

	class Control(private val onSelect: (key: KeyboardState.KeyStroke) -> EasyMotionBranch?) {
		companion object {
			val STEP_BACK = object : UIObject(), EasyMotionBranch {
				override val easyMotionBranch = object : Branch(this) {}
			} as EasyMotionBranch
		}

		/**
		 * The EasyMotionBranch returned by onSelect-function on this Control.
		 * EasyMotion uses this variables to backtrace and rebuild its stack.
		 * No, it does not.
		 */
		var child: EasyMotionBranch? = null
			private set

		/**
		 * Called by EasyMotion when this Control gets selected (user typed its keys).
		 */
		fun select(keys: KeyboardState.KeyStroke): EasyMotionBranch? {
			val result = onSelect(keys)

			child = if (result == STEP_BACK)
				null
			else
				result

			return result
		}
	}

	init {
		if (uiobject !is EasyMotionBranch)
			throw RuntimeException("Branch must have a UIObject implementing EasyMotionBranch")
	}

	var handler: Handler? = null

	val controls = HashMap<Set<KeyboardEvent.Keys>, Control>()

	/**
	 * Tells EasyMotion to focus here.
	 * EasyMotion will try to rebuild its stack from this element.
	 */
	fun focus() {
		getEasyMotionMaster()?.focus(this.uiobject as EasyMotionBranch)
	}

	private fun getEasyMotionMaster() = uiobject.search.parentByInterface(EasyMotionMaster::class.java)?.easyMotion
}