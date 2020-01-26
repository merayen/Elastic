package net.merayen.elastic.ui.objects.top.easymotion

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.event.KeyboardEvent.Keys
import net.merayen.elastic.ui.util.KeyboardState
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EasyMotionTest {
	open class NamedUIObject(val name: String) : UIObject()
	val result = ArrayList<Pair<String, Any>>()

	private var top: NamedUIObject? = null
	private var child1: NamedUIObject? = null
	private var child11: NamedUIObject? = null
	private var child2: NamedUIObject? = null
	private var child4: NamedUIObject? = null
	private var child41: NamedUIObject? = null
	private var child42: NamedUIObject? = null

	private val nothing = Any()

	@BeforeEach
	fun setUp() {
		/* Tree:

		CTRL-T
			A
				F
			P
				G
				<ANY>
		 */
		result.clear()

		top = object : NamedUIObject("top"), EasyMotionMaster, EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					//result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf(Keys.CONTROL, Keys.T)
			}

			override val easyMotion = EasyMotion(this, easyMotionControl)

			init {
				easyMotion.handler = object : EasyMotion.Handler {
					override fun onMistype(keyStroke: KeyboardState.KeyStroke) {
						result.add(Pair("mistype", keyStroke))
					}
				}
			}
		}

		addBackButton(top!!)
		child1 = object : NamedUIObject("child1"), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf(Keys.A)
			}
		}
		top!!.add(child1!!)
		addBackButton(child1!!)

		// Dead end
		child2 = NamedUIObject("child2")
		top!!.add(child2!!)

		child11 = object : NamedUIObject("child11"), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf(Keys.F)
			}
		}
		child1!!.add(child11!!)


		child4 = object : NamedUIObject("child4"), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf(Keys.P)
			}
		}
		top!!.add(child4!!)
		addBackButton(child4!!)


		child41 = object : NamedUIObject("child41"), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf(Keys.G)
			}
		}
		child4!!.add(child41!!)


		child42 = object : NamedUIObject("child42"), EasyMotionControllable {
			override val easyMotionControl = object : Control(this) {
				override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
					result.add(Pair("selected", uiobject))
				}

				override fun onLeave() {
					result.add(Pair("leaving", uiobject))
				}

				override val trigger = setOf<Keys>()  // captures every key but the keys defined by sibling Controls
			}
		}
		child4!!.add(child42!!)
		addBackButton(child42!!)
	}

	@Test
	fun testTree() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.A)

		update()

		// Should now have selected top and child31
		Assertions.assertEquals(
			arrayListOf(
				Pair("selected", top),
				Pair("selected", child1)
			),
			result
		)

		// Go back one step by pushing Q
		pushKeys(Keys.Q)

		update()

		// Assert that we are back on the first Control
		Assertions.assertEquals(
			arrayListOf(
				Pair("selected", top),
				Pair("selected", child1),
				Pair("back", child1)
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
				Pair("selected", top),
				Pair("mistype", KeyboardState.KeyStroke(setOf(KeyboardEvent.Key(' ', 77, Keys.M)))),
				Pair("selected", child1)
			),
			result
		)
	}

	@Test
	fun testAnyReceiver() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.P)
		pushKeys(Keys.G)
		pushKeys(Keys.X)

		update()

		Assertions.assertEquals(
			arrayListOf(
				Pair("selected", top),
				Pair("selected", child4),
				Pair("selected", child41),
				Pair("selected", child42)
			),
			result
		)
	}

	@Test
	fun testOptions() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.A) // This is correct
		pushKeys(Keys.F) // This works, hit it twice, as it has no child, it should jump back
		pushKeys(Keys.F) // This works, hit it twice, as it has no child, it should jump back

		update()

		Assertions.assertEquals(
			arrayListOf(
				Pair("selected", top),
				Pair("selected", child1),
				Pair("selected", child11),
				Pair("selected", child11)
			),
			result
		)
	}

	@Test
	fun testRemovingUIObject() {
		pushKeys(Keys.CONTROL, Keys.T)
		pushKeys(Keys.A)

		update()

		top!!.remove(child1!!)

		update()

		Assertions.assertEquals(
			arrayListOf(
				(top as EasyMotionControllable).easyMotionControl
			),
			(top as EasyMotionMaster).easyMotion.getCurrentStack()
		)
	}

	@Test
	fun testSelect() {
		(child1 as EasyMotionControllable).easyMotionControl.select()

		Assertions.assertEquals(
			arrayListOf(
				(top as EasyMotionControllable).easyMotionControl,
				(child1 as EasyMotionControllable).easyMotionControl
			),
			(top as EasyMotionMaster).easyMotion.getCurrentStack()
		)
	}

	@Test
	fun testSelectChildrenlessControl() {
		(child41 as EasyMotionControllable).easyMotionControl.select()

		Assertions.assertEquals(
			arrayListOf(
				(top as EasyMotionControllable).easyMotionControl,
				(child4 as EasyMotionControllable).easyMotionControl
			),
			(top as EasyMotionMaster).easyMotion.getCurrentStack()
		)
	}


	private fun update() {
		(top as EasyMotionMaster).easyMotion.update()
	}

	private fun addBackButton(uiobject: NamedUIObject) {
		val control = (uiobject as EasyMotionControllable).easyMotionControl
		val obj = object : NamedUIObject("back:${uiobject.name}"), EasyMotionControllable {
			override val easyMotionControl: Control
				get() = object : Control(this) {
					override fun onSelect(keyStroke: KeyboardState.KeyStroke) {
						result.add(Pair("back", control.uiobject))
						(top as EasyMotionMaster).easyMotion.select(control.parent!!.easyMotionControl)
					}

					override fun onLeave() {}

					override val trigger = setOf(Keys.Q)
				}
		}

		control.uiobject.add(obj)
	}

	private fun pushKeys(vararg keys: Keys) {
		for (key in keys) // press
			(top as EasyMotionMaster).easyMotion.handle(KeyboardEvent("", ' ', key.code, true))

		for (key in keys) // release
			(top as EasyMotionMaster).easyMotion.handle(KeyboardEvent("", ' ', key.code, false))
	}
}