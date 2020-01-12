package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.util.KeyboardState

/**
 * EasyMotion is a feature that is similar to vim's EasyMotion plugin (vim = command line text editor for g33ks).
 */
class EasyMotion {
	private val keyboardState = KeyboardState()

	init {
		keyboardState.handler = object : KeyboardState.Handler {
			override fun onType(keysDown: KeyboardState.KeyStroke) {
				println("Those keys are down for what: $keysDown")
			}
		}
	}

	/**
	 * Pass all keyboard events to us. We will send them to the correct EasyMotion Control for processing.
	 */
	fun handle(event: KeyboardEvent) {
		keyboardState.handle(event)
	}
}