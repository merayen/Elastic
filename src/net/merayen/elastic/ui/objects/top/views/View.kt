package net.merayen.elastic.ui.objects.top.views

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.event.MouseEvent
import net.merayen.elastic.ui.objects.top.menu.Bar
import net.merayen.elastic.ui.objects.top.viewport.Viewport
import net.merayen.elastic.util.TaskExecutor
import net.merayen.elastic.util.UniqueID

abstract class View : UIObject {
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

			if(c != null)
				return c as Viewport
			else
				throw RuntimeException("Viewport not found")
		}

	constructor() {
		this.id = UniqueID.create()
	}

	constructor(id: String) {
		this.id = id
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

	val properties: HashMap<String, Any>
		get() {
			val group = search.parentByType(net.merayen.elastic.uinodes.list.group_1.UI::class.java) ?: throw RuntimeException("group-node not found. Can not retrieve data")
			val parameters = group.parameters

			val views = parameters.getOrPut("ui.java.views") {HashMap<String, HashMap<String, Any>>()} as HashMap<String, HashMap<String, Any>>

			return views.getOrPut(id) {HashMap()}
		}

	/**
	 * Adds a task in to the closest ViewportContainer() domain.
	 */
	protected fun addTask(task: TaskExecutor.Task) {
		viewport.viewportContainer.addTask(task)
	}

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight
}
