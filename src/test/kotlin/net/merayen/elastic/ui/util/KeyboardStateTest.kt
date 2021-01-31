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
			override fun onType(keyStroke: KeyboardState.KeyStroke) {
				typed.add(keyStroke)
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
				),
				'a'
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
				),
				'a'
			),
			typed[0]
		)
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
				),
				'b'
			),
			typed[1]
		)
	}

	@Test
	fun testKeyStrokeEqualsKeys() {
		val ks = KeyboardState.KeyStroke(setOf(
			KeyboardEvent.Key('A', 65, KeyboardEvent.Keys.SHIFT),
			KeyboardEvent.Key('A', 65, KeyboardEvent.Keys.CONTROL),
			KeyboardEvent.Key('A', 65, KeyboardEvent.Keys.A)
		), 'A')

		Assertions.assertFalse(ks.equalsKeys(setOf(
			KeyboardEvent.Keys.CONTROL,
			KeyboardEvent.Keys.ALT,
			KeyboardEvent.Keys.A,
			KeyboardEvent.Keys.SHIFT
		)))

		Assertions.assertFalse(ks.equalsKeys(setOf(
			KeyboardEvent.Keys.A,
			KeyboardEvent.Keys.SHIFT
		)))

		Assertions.assertTrue(ks.equalsKeys(setOf(
			KeyboardEvent.Keys.A,
			KeyboardEvent.Keys.SHIFT,
			KeyboardEvent.Keys.CONTROL
		)))
	}

	@Test
	fun testEmptyEqualsKeys() {
		Assertions.assertTrue(KeyboardState.KeyStroke(setOf(), ' ').equalsKeys(setOf()))
	}

	private fun push(events: ArrayList<KeyboardEvent>): Set<KeyboardEvent> {
		for (event in events)
			ks!!.handle(event)

		return HashSet(events)
	}
}