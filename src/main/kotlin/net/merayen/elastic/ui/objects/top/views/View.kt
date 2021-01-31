package net.merayen.elastic.ui.objects.top.views

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.objects.top.marks.MarksManager
import net.merayen.elastic.ui.objects.top.menu.Bar
import net.merayen.elastic.ui.objects.top.viewport.Viewport
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer
import net.merayen.elastic.util.UniqueID
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class View : UIObject, EasyMotionBranch {
	val id: String
	var layoutWidth = 100f // Set by EditNodeView
	var layoutHeight = 100f // Set by EditNodeView

	/**
	 * See if mouse is on this view.
	 */
	protected var isFocused: Boolean = false
		private set // true when mouse is hovering over us, meaning we have the focus

	private val bar = Bar() // Shown in all viewports

	private val viewport: Viewport
		get() {
			var c: UIObject? = this
			while (c != null && c !is Viewport)
				c = c.parent

			if (c != null)
				return c as Viewport
			else
				throw RuntimeException("Viewport not found")
		}

	/**
	 * Mark support is put into View as they are global for the current view.
	 */
	val marks = MarksManager(this)


	constructor() {
		this.id = UniqueID.create()
	}

	constructor(id: String) {
		this.id = id
	}

	/**
	 * Swap this view with another View. Happens later.
	 */
	fun <T : View> swap(cls: KClass<out T>): T {
		val newView: T

		try {
			newView = cls.primaryConstructor!!.call()
		} catch (e: InstantiationException) {
			throw RuntimeException(e)
		} catch (e: IllegalAccessException) {
			throw RuntimeException(e)
		}

		val viewportContainer = search.parentByType(ViewportContainer::class.java)

		viewportContainer ?: throw RuntimeException("View has not been attached to a ViewportContainer yet")

		viewportContainer.swapView(this, newView)

		return newView
	}

	abstract fun cloneView(): View

	override fun onInit() {
		super.onInit()
		add(bar)
	}

	override fun onDraw(draw: Draw) {
		if (isFocused) {
			draw.setColor(200, 200, 200)
			draw.setStroke(4f)
			draw.rect(1f, 1f, layoutWidth - 2, layoutHeight - 2)
		}

		bar.translation.y = layoutHeight - 20
		bar.width = layoutWidth
	}

	override fun onEvent(event: UIEvent) {
		if (event is MouseEvent) {
			isFocused = event.hitDepth(this) > -1
		}
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}
