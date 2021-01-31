package net.merayen.elastic.ui.objects.top.window

import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotion
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.util.KeyboardState

/**
 * Adds EasyMotion functionality to Window.
 */
class WindowEasyMotion(private val window: Window) {
	private val easyMotionOverlay = EasyMotionOverlay(window)
	private val keyboardState = KeyboardState()

	init {
		window.easyMotion.handler = object : EasyMotion.Handler {
			override fun onMistype(keyStroke: KeyboardState.KeyStroke) {
				window.viewportContainer.blinkRed()
			}

			override fun onEnter(branch: EasyMotionBranch) {
				println("Inn ${branch}")
			}

			override fun onLeave(branch: EasyMotionBranch) {
				println("Out ${branch}")
			}
		}

		window.add(easyMotionOverlay)

		keyboardState.handler = object : KeyboardState.Handler {
			override fun onType(keyStroke: KeyboardState.KeyStroke) {
				if (keyStroke.hasKey(KeyboardEvent.Keys.CONTROL) && keyStroke.character == '?')
					easyMotionOverlay.isHelping = easyMotionOverlay.isHelping xor true
			}
		}
	}

	fun handleEvent(event: UIEvent) {
		if (event is KeyboardEvent) {
			keyboardState.handle(event)
		}
	}
}