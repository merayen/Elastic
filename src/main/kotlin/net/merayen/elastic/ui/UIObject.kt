package net.merayen.elastic.ui

import java.util.ArrayList

import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.Point
import net.merayen.elastic.util.Postmaster

open class UIObject {
	var parent: UIObject? = null
		private set
	internal val children: MutableList<UIObject> = ArrayList()

	var outline_abs_px: Rect? = Rect() // Absolute, in screen pixels
	internal var outline: Rect? = Rect()

	var draw_z: Int = 0 // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()

	var translation = TranslationData()
	var absolute_translation: TranslationData? = null

	/**
	 * Evaluates to true if this object has been drawn once or more, so that translation
	 * and other initializing has been done.
	 * You need to check this before calling functions like getPixelDimension etc, as they are
	 * not available until the object has been drawn (and initialized).
	 */
	var isInitialized = false
		private set
	var isAttached: Boolean = false
		private set // Attached to the tree (reachable from Top) or not

	val search = Search(this)

	val absolutePosition: Point?
		get() {
			val td = absolute_translation
			if (td != null)
				return Point(td.x, td.y)

			return null
		}

	/**
	 * Retrieves the deep outline of the object (including the children's outline)
	 */
	// TODO apply absolute clip
	val deepOutline: Rect?
		get() {
			var result: Rect? = outline_abs_px

			for (o in search.allChildren) {
				if (o.outline_abs_px == null) continue

				if (result == null)
					result = Rect(o.outline_abs_px!!)
				else
					result.enlarge(o.outline_abs_px)
			}

			if (result == null)
				return null

			result.x1 -= absolute_translation!!.x
			result.y1 -= absolute_translation!!.y
			result.x2 -= absolute_translation!!.x
			result.y2 -= absolute_translation!!.y

			result.x1 *= absolute_translation!!.scaleX
			result.y1 *= absolute_translation!!.scaleY
			result.x2 *= absolute_translation!!.scaleX
			result.y2 *= absolute_translation!!.scaleY

			return result
		}

	/**
	 * Retrieves the width of the object.
	 * Feel free to override to return a custom value.
	 */
	open fun getWidth(): Float {
		val deep = deepOutline!!
		return deep.x2 - deep.x1
	}

	open fun getHeight(): Float {
		val deep = deepOutline!!
		return deep.y2 - deep.y1
	}

	open fun onInit() {}
	open fun onDraw(draw: Draw) {}
	open fun onUpdate() {}
	open fun onEvent(event: UIEvent) {}

	/**
	 * Override this method to artificially set the children of the UIObject in a onDraw(), onUpdate() and onEvent() call tree.
	 * Should only be used in very specific cases, like by the Top() object to draw different trees for different Window()s.
	 */
	open fun onGetChildren(surface_id: String): List<UIObject> {
		return this.children
	}

	fun add(uiobject: UIObject) {
		add(uiobject, children.size)
	}

	open fun add(uiobject: UIObject, index: Int) {
		if (uiobject.parent != null)
			throw RuntimeException("UIObject already has a parent")

		uiobject.parent = this
		children.add(index, uiobject)

		// Mark every child as attached to the tree
		uiobject.isAttached = true
		for (o in uiobject.search.allChildren)
			o.isAttached = true
	}

	fun remove(uiobject: UIObject) {
		if (!children.contains(uiobject))
			throw RuntimeException("UIObject is not a child of us")

		if (uiobject.parent !== this)
			throw RuntimeException("Should not happen")

		children.remove(uiobject)
		uiobject.parent = null

		// Mark every child to be detached
		uiobject.isAttached = false
		for (o in uiobject.search.allChildren)
			o.isAttached = false
	}

	fun removeAll() {
		for(o in children) {
			if (o.parent !== this)
				throw RuntimeException("Should not happen")

			o.parent = null

			// Mark every child to be detached
			o.isAttached = false
			for (oc in o.search.allChildren)
				oc.isAttached = false
		}

		children.clear()
	}

	internal fun initialize() {
		isInitialized = true // Placed in the beginning to prevent repeated calls to onInit()
		onInit()
	}

	internal fun updateDraw(draw: Draw) = onDraw(draw)

	fun getAbsolutePosition(offset_x: Float, offset_y: Float): Point? {
		val td = absolute_translation ?: return null

		return Point((td!!.x + offset_x / td.scaleX).toInt().toFloat(), (td.y + offset_y / td.scaleY).toInt().toFloat()) // Pixel perfect
	}

	/**
	 * Returns the relative position of the object "obj" to this object.
	 */
	fun getRelativePosition(obj: UIObject): Point? {
		if (obj.absolute_translation == null)
			return null // Haven't drawn anything yet

		return Point(
				(obj.absolute_translation!!.x - absolute_translation!!.x) * absolute_translation!!.scaleX,
				(obj.absolute_translation!!.y - absolute_translation!!.y) * absolute_translation!!.scaleY
		)
	}

	/**
	 * Get our internal (relative) position from absolute position.
	 */
	fun getRelativeFromAbsolute(x: Float, y: Float): Point {
		val td = absolute_translation
		return Point((x - td!!.x) * td.scaleX, (y - td.y) * td.scaleY)
	}

	fun getAbsoluteDimension(width: Float, height: Float): Dimension? {
		val td = absolute_translation ?: return null

		return Dimension((width / td.scaleX).toInt().toFloat(), (height / td.scaleY).toInt().toFloat())
	}

	/**
	 * Converts a single unit.
	 * Uses both scaleX and scaleY to figure out the resulting value.
	 */
	fun convertUnitToAbsolute(a: Float): Int {
		val td = absolute_translation
		val resolution = Math.min(td!!.scaleX, td.scaleY)
		return (a / resolution).toInt()
	}

	/**
	 * Converts a single unit.
	 * Uses both scaleX and scaleY to figure out the resulting value. No it doesn't.
	 */
	fun convertAbsoluteToUnit(a: Int): Float { // TODO Only uses the x-scale. Maybe make two functions, one for X and one for Y, and one for both somehow?
		val td = absolute_translation

		return a.toFloat() * td!!.scaleX
	}

	/**
	 * Retrieves a copy of the drawn outline of this object.
	 * This is the outline of the previous paint, so it will
	 * not be available before the first paint.
	 */
	fun getOutline(): Rect? {
		return if (outline == null) null else Rect(outline!!)
	}

	open fun sendMessage(message: Postmaster.Message) {
		val top = UINodeUtil.getTop(this)
		top?.sendMessage(message)
				?: System.out.printf("WARNING: Could not send message, UIObject %s is disconnected from Top()\n", javaClass.name)
	}
}
