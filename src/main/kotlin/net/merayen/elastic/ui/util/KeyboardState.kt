package net.merayen.elastic.ui.util

import net.merayen.elastic.ui.event.KeyboardEvent

class KeyboardState {
	interface Handler {
		/**
		 * Called whenever user has typed something. E.g:
		 * - A
		 * - &lt;SHIFT&gt; + A
		 * - &lt;CTRL&gt; + &lt;SHIFT&gt; + &lt;ALT&gt; + L
		 */
		fun onType(keyStroke: KeyboardState.KeyStroke)
	}

	var handler: Handler? = null

	/**
	 * A keyboard event consist of e.g a single letter typed, backspace, escape, SHIFT+a, CTRL+SHIFT+ALT+L etc.
	 */
	data class KeyStroke(val keys: Set<KeyboardEvent.Key>, val character: Char?) {
		init {
			if (keys.isEmpty())
				throw RuntimeException("KeyStroke must contain one or more keys pressed")
		}

		val modifiers = keys.filter { it.isModifier }

		/**
		 * Checks if the keys are identical to this KeyStroke.
		 * KeyStroke can not contain any additional keys than this KeyStroke gets compared against.
		 *
		 * @param keys a set of keys
		 */
		fun equalsKeys(keys: Collection<KeyboardEvent.Keys>) = this.keys.map { it.key }.toSet() == keys.toSet()

		fun hasKey(key: KeyboardEvent.Keys) = this.keys.firstOrNull { it.key == key } != null
	}

	/**
	 * Keys held down currently.
	 */
	val keysDown = HashSet<KeyboardEvent.Key>()

	/**
	 * All type events that has happened.
	 * Scan backward to detect patterns.
	 * Clear it to start a new.
	 */
	val typingEvents = ArrayList<KeyStroke>()

	fun handle(event: KeyboardEvent) {
		if (event.pushed) {
			keysDown.add(event.key)
			if (!event.key.isModifier) {
				val keyStroke = KeyStroke(keysDown.filter { it.isModifier || it.key == event.key.key }.toSet(), event.character)
				typingEvents.add(keyStroke)
				handler?.onType(keyStroke)
			}
		} else {
			keysDown.remove(event.key)
		}
	}

	fun reset() {
		typingEvents.clear()
	}
}