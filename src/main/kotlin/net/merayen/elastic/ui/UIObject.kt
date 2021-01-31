package net.merayen.elastic.ui

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.util.UINodeUtil
import net.merayen.elastic.util.MutablePoint
import java.util.*

open class UIObject {
	var parent: UIObject? = null
		private set
	val internalChildren: MutableList<UIObject> = ArrayList()

	val children: List<UIObject>
		get() = Collections.unmodifiableList(internalChildren)

	var absoluteOutline: Rect? = Rect() // Absolute, in screen pixels
	internal var outline: Rect? = Rect()

	var draw_z: Int = 0 // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()

	var translation = TranslationData()
	var absoluteTranslation: TranslationData? = null

	/**
	 * Evaluates to true if this object has been drawn once or more, so that translation
	 * and other initializing has been done.
	 * You need to check this before calling functions like getPixelDimension etc, as they are
	 * not available until the object has been drawn (and initialized).
	 */
	var isInitialized = false
		private set

	/**
	 * Cached version of the topmost parent of this UIObject
	 */
	var topMost: UIObject = this

	val search = Search(this)

	val absolutePosition: MutablePoint?
		get() {
			val td = absoluteTranslation
			if (td != null)
				return MutablePoint(td.x, td.y)

			return null
		}

	/**
	 * Actual visible area for this UIObject.
	 * The returned rectangle is in local coordinates.
	 * Use this feature to skip drawing outside visible area.
	 */
	val visibility: Rect?
		get() {
			val clip = absoluteTranslation?.clip ?: return null
			return getRelativeFromAbsolute(clip)
		}

	/**
	 * Retrieves the deep outline of the object (including the children's outline)
	 * TODO do clipping?
	 */
	val deepOutline: Rect?
		get() {
			val absoluteOutline = absoluteOutline

			var result: Rect? = absoluteOutline?.copy()

			val absoluteTranslation = absoluteTranslation ?: return null

			for (o in search.allChildren) {
				val outline_abs_px = o.absoluteOutline ?: continue

				if (result == null)
					result = Rect(outline_abs_px)
				else
					result.enlarge(outline_abs_px)
			}

			if (result == null)
				return null

			result.x1 -= absoluteTranslation.x
			result.y1 -= absoluteTranslation.y
			result.x2 -= absoluteTranslation.x
			result.y2 -= absoluteTranslation.y

			result.x1 *= absoluteTranslation.scaleX
			result.y1 *= absoluteTranslation.scaleY
			result.x2 *= absoluteTranslation.scaleX
			result.y2 *= absoluteTranslation.scaleY

			return result
		}

	/**
	 * Retrieves the width of the object.
	 * Feel free to override to return a custom value.
	 */
	open fun getWidth(): Float {
		val deep = deepOutline ?: return 0f
		return deep.x2 - deep.x1
	}

	open fun getHeight(): Float {
		val deep = deepOutline ?: return 0f
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
		return children
	}

	open fun add(uiobject: UIObject) {
		internalAdd(uiobject, internalChildren.size)
	}

	open fun add(uiobject: UIObject, index: Int) {
		internalAdd(uiobject, index)
	}

	private fun internalAdd(uiobject: UIObject, index: Int) {
		if (uiobject == this)
			throw RuntimeException("UIObject can not be a child of itself")

		if (uiobject.parent != null)
			throw RuntimeException("UIObject already has a parent")


		uiobject.parent = this
		internalChildren.add(index, uiobject)

		topMost = calculateTopMost()

		// Mark every child as attached to the tree
		uiobject.topMost = topMost
		for (o in uiobject.search.allChildren)
			o.topMost = topMost
	}

	open fun remove(uiobject: UIObject) {
		if (!internalChildren.contains(uiobject))
			throw RuntimeException("UIObject is not a child of us")

		if (uiobject.parent !== this)
			throw RuntimeException("Should not happen")


		internalChildren.remove(uiobject)
		uiobject.parent = null

		// Mark every child to be detached
		uiobject.topMost = uiobject
		for (o in uiobject.search.allChildren)
			o.topMost = uiobject
	}

	open fun removeAll() {
		for (o in internalChildren) {
			if (o.parent !== this)
				throw RuntimeException("Should not happen")

			o.parent = null

			// Mark every child to be detached
			o.topMost = o
			for (oc in o.search.allChildren)
				oc.topMost = o
		}

		internalChildren.clear()
	}

	internal fun initialize() {
		isInitialized = true // Placed in the beginning to prevent repeated calls to onInit()
		onInit()
	}

	internal fun updateDraw(draw: Draw) = onDraw(draw)

	fun getAbsolutePosition(offset_x: Float, offset_y: Float): MutablePoint? {
		val td = absoluteTranslation ?: return null

		return MutablePoint((td.x + offset_x / td.scaleX).toInt().toFloat(), (td.y + offset_y / td.scaleY).toInt().toFloat()) // Pixel perfect
	}

	/**
	 * Returns the relative position of the object "obj" to this object.
	 */
	fun getRelativePosition(obj: UIObject): MutablePoint? {
		if (obj.absoluteTranslation == null)
			return null // Haven't drawn anything yet

		return MutablePoint(
				(obj.absoluteTranslation!!.x - absoluteTranslation!!.x) * absoluteTranslation!!.scaleX,
				(obj.absoluteTranslation!!.y - absoluteTranslation!!.y) * absoluteTranslation!!.scaleY
		)
	}

	fun getRelativePosition(obj: UIObject, x: Float = 0f, y: Float = 0f): MutablePoint? {
		val objAT = obj.absoluteTranslation ?: return null
		val thisAT = absoluteTranslation ?: return null

		return MutablePoint(
				x = ((objAT.x + x / objAT.scaleX) - thisAT.x) * thisAT.scaleX,
				y = ((objAT.y + y / objAT.scaleY) - thisAT.y) * thisAT.scaleY
		)
	}

	/**
	 * Get our internal (relative) position from absolute position.
	 */
	fun getRelativeFromAbsolute(x: Float, y: Float): MutablePoint {
		val td = absoluteTranslation
		return MutablePoint((x - td!!.x) * td.scaleX, (y - td.y) * td.scaleY)
	}

	fun getRelativeFromAbsolute(rect: Rect): Rect {
		val p1 = getRelativeFromAbsolute(rect.x1, rect.y1)
		val p2 = getRelativeFromAbsolute(rect.x2, rect.y2)
		return Rect(p1.x, p1.y, p2.x, p2.y)
	}

	fun getAbsoluteDimension(width: Float, height: Float): MutableDimension? {
		val td = absoluteTranslation ?: return null

		return MutableDimension((width / td.scaleX).toInt().toFloat(), (height / td.scaleY).toInt().toFloat())
	}

	/**
	 * Converts a single unit.
	 * Uses both scaleX and scaleY to figure out the resulting value.
	 */
	fun convertUnitToAbsolute(a: Float): Int {
		val td = absoluteTranslation
		val resolution = Math.min(td!!.scaleX, td.scaleY)
		return (a / resolution).toInt()
	}

	/**
	 * Converts a single unit.
	 * Uses both scaleX and scaleY to figure out the resulting value. No it doesn't.
	 */
	fun convertAbsoluteToUnit(a: Int): Float { // TODO Only uses the x-scale. Maybe make two functions, one for X and one for Y, and one for both somehow?
		val td = absoluteTranslation

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

	open fun sendMessage(message: ElasticMessage) {
		val top = UINodeUtil.getTop(this)
		top?.sendMessage(message)
				?: System.out.printf("WARNING: Could not send message, UIObject %s is disconnected from Top()\n", javaClass.name)
	}

	/**
	 * Retrieves the topmost parent of this object.
	 * If no parents, we are the topmost object.
	 */
	private fun calculateTopMost(): UIObject {
		var topMost = this
		while (true)
			topMost = topMost.parent ?: break

		return topMost
	}

}
