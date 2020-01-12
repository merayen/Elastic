package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.event.KeyboardEvent.Keys
import net.merayen.elastic.ui.util.KeyboardState
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EasyMotionTest {
	val result = ArrayList<Pair<String, Any>>()

	private var top: UIObject? = null
	private var child1: UIObject? = null
	private var child11: UIObject? = null
	private var child2: UIObject? = null
	private var child3: UIObject? = null
	private var child31: UIObject? = null
	private var child311: UIObject? = null

	private val nothing = Any()

	@BeforeEach
	fun setUp() {
		result.clear()

		top = object : UIObject(), EasyMotionMaster, EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect() {
					result.add(Pair("selected", uiobject))
				}

				override fun onUnselect() { }

				override fun onEnter(control: Control) {}
				override val trigger = setOf(Keys.CONTROL, Keys.T)
			}

			override val easyMotion = EasyMotion(this, easyMotionControl)

			init {
				easyMotion.handler = object : EasyMotion.Handler {
					override fun onMistype(keyStroke: KeyboardState.KeyStroke) {
						result.add(Pair("mistype", keyStroke))
					}

					override fun onEnter() {
						result.add(Pair("enter", nothing))
					}

					override fun onLeave() {
						result.add(Pair("leave", nothing))
					}
				}
			}
		}

		child1 = object : UIObject(), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect() {
					result.add(Pair("selected", uiobject))
				}

				override fun onUnselect() { }

				override fun onEnter(control: Control) {}
				override val trigger = setOf(Keys.A)
			}
		}
		top!!.add(child1!!)

		// Dead end
		child2 = UIObject()
		top!!.add(child2!!)

		child11 = object : UIObject(), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect() {
					result.add(Pair("selected", uiobject))
				}

				override fun onUnselect() { }

				override fun onEnter(control: Control) {}
				override val trigger = setOf(Keys.F)
			}
		}
		child1!!.add(child11!!)

		child3 = UIObject()
		top!!.add(child3!!)

		child31 = object : UIObject(), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect() {
					result.add(Pair("selected", uiobject))
				}

				override fun onUnselect() { }

				override fun onEnter(control: Control) {}
				override val trigger = setOf(Keys.S)
			}
		}
		child3!!.add(child31!!)

		child311 = object : UIObject(), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect() {
					result.add(Pair("selected", uiobject))
				}

				override fun onUnselect() { }

				override fun onEnter(control: Control) {}
				override val trigger = setOf(Keys.U)
			}
		}
		child31!!.add(child311!!)
	}

	@Test
	fun testTree() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.A)

		// Should now have selected top and child31
		Assertions.assertEquals(
			arrayListOf(
				Pair("enter", nothing),
				Pair("selected", top),
				Pair("selected", child1)
			),
			result
		)

		// Go back one step by pushing Q
		pushKeys(Keys.Q)

		// Assert that we are back on the first Control
		Assertions.assertEquals(
			arrayListOf(
				Pair("enter", nothing),
				Pair("selected", top),
				Pair("selected", child1),
				Pair("selected", top)
			),
			result
		)
	}

	@Test
	fun testMistyping() {
		pushKeys(Keys.CONTROL, Keys.T) // OK
		pushKeys(Keys.M) // We mistype
		pushKeys(Keys.A) // Then we enter the correct object

		Assertions.assertEquals(
			arrayListOf(
				Pair("enter", nothing),
				Pair("selected", top),
				Pair("mistype", KeyboardState.KeyStroke(setOf(KeyboardEvent.Key(' ', 77, Keys.M)))),
				Pair("selected", child1)
			),
			result
		)
	}

	@Test
	fun testEscaping() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.A)
		pushKeys(Keys.ESCAPE) // We escape, leaving EasyMotion
		pushKeys(Keys.CONTROL, Keys.T)  // Then we enter EasyMotion again

		Assertions.assertEquals(
			arrayListOf(
				Pair("enter", nothing),
				Pair("selected", top),
				Pair("selected", child1),
				Pair("leave", nothing),
				Pair("enter", nothing),
				Pair("selected", top)
			),
			result
		)
	}

	@Test
	fun testOptions() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.S) // This works
		pushKeys(Keys.A) // This is mistyping
		pushKeys(Keys.U) // This works, hit it twice, as it has no child, it should jump back
		pushKeys(Keys.U) // This works, hit it twice, as it has no child, it should jump back
		pushKeys(Keys.Q) // Go 1 back
		pushKeys(Keys.A) // This works
		pushKeys(Keys.F) // This works
		pushKeys(Keys.Q) // Go 1 back
		pushKeys(Keys.Q) // Leaves EasyMotion
		pushKeys(Keys.Q) // No-op. Should do nothing

		Assertions.assertEquals(
			arrayListOf(
				Pair("enter", nothing),
				Pair("selected", top),
				Pair("selected", child31),
				Pair("mistype", KeyboardState.KeyStroke(setOf(
					KeyboardEvent.Key(' ', KeyboardEvent.Keys.A.code, KeyboardEvent.Keys.A)
				))),
				Pair("selected", child311),
				Pair("selected", child31),
				Pair("selected", child311),
				Pair("selected", child31),
				Pair("selected", top),
				Pair("selected", child1),
				Pair("selected", child11),
				Pair("selected", child1),
				Pair("selected", top),
				Pair("leave", nothing)
			),
			result
		)
	}

	private fun pushKeys(vararg keys: Keys) {
		for (key in keys) // press
			(top as EasyMotionMaster).easyMotion.handle(KeyboardEvent("", ' ', key.code, true))

		for (key in keys) // release
			(top as EasyMotionMaster).easyMotion.handle(KeyboardEvent("", ' ', key.code, false))
	}
}