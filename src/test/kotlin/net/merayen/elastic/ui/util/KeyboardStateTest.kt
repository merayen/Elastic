package net.merayen.elastic.ui.util

import net.merayen.elastic.ui.event.KeyboardEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KeyboardStateTest {
	private var ks: KeyboardState? = null
	private val typed = ArrayList<KeyboardState.KeyStroke>()


	@BeforeEach
	fun setUp() {
		typed.clear()

		val ks = KeyboardState()
		ks.handler = object : KeyboardState.Handler {
			override fun onType(keysDown: KeyboardState.KeyStroke) {
				typed.add(keysDown)
			}
		}

		this.ks = ks
	}

	@Test
	fun testTyping() {
		val ks = ks!!

		push(arrayListOf(
			KeyboardEvent("", 'a', 65, true)
		))

		Assertions.assertEquals(
			KeyboardState.KeyStroke(
				setOf(
					KeyboardEvent.Key('a', 65, KeyboardEvent.Keys.A)
				)
			),
			typed[0]
		)
	}

	@Test
	fun testDepress() {
		push(arrayListOf(
			KeyboardEvent("", 'a', 65, false)
		))
		Assertions.assertEquals(
			0,
			typed.size
		)
	}

	@Test
	fun testModifiers() {
		push(arrayListOf(
			KeyboardEvent("", '\u0000', 16, true),
			KeyboardEvent("", '\u0000', 17, true),
			KeyboardEvent("", '\u0000', 18, true),
			KeyboardEvent("", 'a', 65, true)
		))

		Assertions.assertEquals(
			KeyboardState.KeyStroke(
				setOf(KeyboardEvent.Key('\u0000', 16, KeyboardEvent.Keys.SHIFT),
					KeyboardEvent.Key('\u0000', 17, KeyboardEvent.Keys.CONTROL),
					KeyboardEvent.Key('\u0000', 18, KeyboardEvent.Keys.ALT),
					KeyboardEvent.Key('a', 65, KeyboardEvent.Keys.A)
				)
			),
			typed[0]
		)
	}

	@Test
	fun testModifierReading() {
		val ks = ks!!

		var ran = false

		ks.handler = object : KeyboardState.Handler {
			override fun onType(keysDown: KeyboardState.KeyStroke) {
				ran = true
				//Assertions.assertEquals(keysDown., 4)
			}
		}

		//push()
	}

	@Test
	fun testModifiersPushAndThenAnotherPush() {
		val pushed = arrayListOf(
			KeyboardEvent("", '\u0000', 16, true),
			KeyboardEvent("", '\u0000', 17, true),
			KeyboardEvent("", '\u0000', 18, true),
			KeyboardEvent("", 'a', 65, true),
			KeyboardEvent("", 'a', 65, false),
			KeyboardEvent("", '\u0000', 16, false),
			KeyboardEvent("", '\u0000', 17, false),
			KeyboardEvent("", 'b', 66, true)
		)

		push(pushed)

		Assertions.assertEquals(
			KeyboardState.KeyStroke(
				setOf(
					KeyboardEvent.Key('\u0000', 18, KeyboardEvent.Keys.ALT),
					KeyboardEvent.Key('b', 66, KeyboardEvent.Keys.B)
				)
			),
			typed[1]
		)
	}

	private fun push(events: ArrayList<KeyboardEvent>): Set<KeyboardEvent> {
		for (event in events)
			ks!!.handle(event)

		return HashSet(events)
	}
}