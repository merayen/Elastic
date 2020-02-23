package net.merayen.elastic.ui

import net.merayen.elastic.ui.util.DrawContext
import net.merayen.elastic.util.Point
import java.awt.Font
import java.awt.FontMetrics
import java.awt.geom.Path2D

/**
 * Helper class to make drawing easy inside the UIObject()s.
 * UIObjects uses this.
 * Instantiated for every draw of every UIObject (Hello Java GC, Work bitch)
 * This class mostly translates our internal floating point coordinate system to pixels,
 * making it easier to draw stuff, and abstracting away the underlaying painting system.
 * TODO don't instantiate it on every uiobject and store the Z-index for all the drawings
 * TODO Abstract this, so we can present these functions to other draw systems (e.g on Android)
 */
class Draw internal constructor(private val uiobject: UIObject, private val draw_context: DrawContext) {
	private val g2d: java.awt.Graphics2D = draw_context.graphics2d

	/**
	 * outline is null if the object has not drawn anything on this particular frame.
	 */
	var outline: Rect? = null // Relative

	private var font_name = "Geneva"
	private var font_size = 1f

	private var skip_outline = false

	internal var font_metrics: FontMetrics? = null

	val surfaceID: String
		get() = draw_context.surfaceID

	val absoluteOutline: Rect?
		get() {
			val outline = outline ?: return null

			val absoluteTranslation = uiobject.absoluteTranslation!!

			val result = Rect()

			result.x1 = outline.x1 / absoluteTranslation.scaleX + absoluteTranslation.x
			result.y1 = outline.y1 / absoluteTranslation.scaleY + absoluteTranslation.y
			result.x2 = outline.x2 / absoluteTranslation.scaleX + absoluteTranslation.x
			result.y2 = outline.y2 / absoluteTranslation.scaleY + absoluteTranslation.y

			if (absoluteTranslation.clip != null)
				result.clip(absoluteTranslation.clip)

			return result
		}

	init {
		if (uiobject.absoluteTranslation!!.clip != null)
			clip(uiobject.absoluteTranslation!!.clip)
	}

	// Only to be called by UIObject.java, and must be called when finished drawing an object!
	fun destroy() {
		unclip()
	}

	private fun reg(x: Float, y: Float, width: Float, height: Float) {
		if (skip_outline)
			return

		if (width < 0 || height < 0)
			throw RuntimeException("Should not happen")

		var outline = outline

		if (outline == null)
			outline = Rect(x, y, x + width, y + height)
		else
			outline.enlarge(x, y, x + width, y + height)

		this.outline = outline
	}

	fun setColor(r: Int, g: Int, b: Int) {
		g2d.color = java.awt.Color(r, g, b)
	}

	fun setColor(r: Float, g: Float, b: Float) {
		g2d.color = java.awt.Color(r, g, b)
	}

	fun setColor(r: Float, g: Float, b: Float, a: Float) {
		g2d.color = java.awt.Color(r, g, b, a)
	}

	fun setColor(color: Color) {
		g2d.color = java.awt.Color(color.red, color.green, color.blue, color.alpha)
	}

	fun fillRect(x: Float, y: Float, width: Float, height: Float) {
		if (width <= 0 || height <= 0)
			return

		val point = uiobject.getAbsolutePosition(x, y) ?: return
		val dimension = uiobject.getAbsoluteDimension(width, height) ?: return
		reg(x, y, width, height)
		g2d.fillRect(point.x.toInt(), point.y.toInt(), dimension.width.toInt(), dimension.height.toInt())
	}

	fun rect(x: Float, y: Float, width: Float, height: Float) {
		if (width <= 0 || height <= 0)
			return

		val point = uiobject.getAbsolutePosition(x, y) ?: return
		val dimension = uiobject.getAbsoluteDimension(width, height) ?: return
		reg(x, y, width, height)
		g2d.drawRect(point.x.toInt(), point.y.toInt(), dimension.width.toInt(), dimension.height.toInt())
	}

	fun polygon(points: FloatArray) {
		val length = points.size

		if (length < 4)
			return // Nothing to draw

		if ((length / 2) * 2 != length)
			throw RuntimeException("Points array must be dividable by 2")

		val xs = IntArray(length / 2)
		val ys = IntArray(length / 2)

		var xmin = Float.MAX_VALUE
		var ymin = Float.MAX_VALUE
		var xmax = Float.MIN_VALUE
		var ymax = Float.MIN_VALUE

		for (i in 0 until length step 2) {
			if (points[i] < xmin)
				xmin = points[i]
			if (points[i+1] < ymin)
				ymin = points[i+1]

			if (points[i] > xmax)
				xmax = points[i]
			if (points[i+1] > ymax)
				ymax = points[i+1]

			val point = uiobject.getAbsolutePosition(points[i], points[i+1]) ?: return
			xs[i / 2] = point.x.toInt()
			ys[i / 2] = point.y.toInt()
		}

		reg(xmin, ymin, xmax - xmin, ymax - ymin)

		g2d.fillPolygon(xs, ys, length / 2)
	}

	fun setStroke(width: Float) {
		g2d.stroke = java.awt.BasicStroke(uiobject.convertUnitToAbsolute(width).toFloat())
	}

	fun setFont(font_name: String?, font_size: Float) {
		if (font_name != null && font_name.length > 0)
			this.font_name = font_name
		this.font_size = font_size
	}

	private fun setFont() {
		val font = Font(font_name, 0, uiobject.convertUnitToAbsolute(font_size))
		g2d.font = font
	}

	fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
		val p1 = uiobject.getAbsolutePosition(x1, y1) ?: return
		val p2 = uiobject.getAbsolutePosition(x2, y2) ?: return
		reg(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2))
		g2d.drawLine(p1.x.toInt(), p1.y.toInt(), p2.x.toInt(), p2.y.toInt())
	}

	fun fillOval(x: Float, y: Float, width: Float, height: Float) {
		if (width <= 0 || height <= 0)
			return

		val point = uiobject.getAbsolutePosition(x, y) ?: return
		val dimension = uiobject.getAbsoluteDimension(width, height) ?: return
		reg(x, y, width, height)
		g2d.fillOval(point.x.toInt(), point.y.toInt(), dimension.width.toInt(), dimension.height.toInt())
	}

	fun oval(x: Float, y: Float, width: Float, height: Float) {
		if (width <= 0 || height <= 0)
			return

		// TODO implement lineWidth
		val point = uiobject.getAbsolutePosition(x, y) ?: return
		val dimension = uiobject.getAbsoluteDimension(width, height) ?: return
		reg(x, y, width, height)
		g2d.drawOval(point.x.toInt(), point.y.toInt(), dimension.width.toInt(), dimension.height.toInt())
	}

	fun bezier(x: Float, y: Float, points: Array<Point>) {
		val point = uiobject.getAbsolutePosition(x, y) ?: return

		if (points.size == 0)
			return

		val f = Path2D.Float()
		f.moveTo(point.x, point.y)

		if (points.size % 3 != 0)
			throw RuntimeException()

		var i = 0
		while (i < points.size) {
			val p1 = uiobject.getAbsolutePosition(points[i].x, points[i].y)!!
			val p2 = uiobject.getAbsolutePosition(points[i + 1].x, points[i + 1].y)!!
			val p3 = uiobject.getAbsolutePosition(points[i + 2].x, points[i + 2].y)!!

			f.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
			i += 3
		}

		g2d.draw(f)
	}

	fun text(text: String, x: Float, y: Float) {
		val point = uiobject.getAbsolutePosition(x, y) ?: return
		setFont()
		g2d.drawString(text, point.x, point.y)
		reg(x, y - font_size, getTextWidth(text), font_size)
	}

	fun getTextWidth(text: String): Float {
		setFont()
		return uiobject.convertAbsoluteToUnit(g2d.fontMetrics.stringWidth(text))
	}

	fun empty(x: Float, y: Float, width: Float, height: Float) {
		/*
		 * Draw nothing, but increase the draw area. E.g to catch mouse clicks
		 * outside the drawn area.
		 */
		//Point point = uiobject.getAbsolutePixelPoint(x, y);
		//java.awt.Dimension dimension = uiobject.getPixelDimension(layoutWidth, layoutHeight);
		reg(x, y, width, height)
	}

	/*
	 * Only draw inside this rectangle.
	 * Absolute, in our internal coordinates (X and Y == 0 to 1)
	 */
	private fun clip(rect: Rect) {
		/*java.awt.Rectangle r = new java.awt.Rectangle(
			(int)(rect.x1 * draw_context.layoutWidth),
			(int)(rect.y1 * draw_context.layoutHeight),
			(int)((rect.x2 - rect.x1) * draw_context.layoutWidth),
			(int)((rect.y2 - rect.y1) * draw_context.layoutHeight)
		);*/

		val r = java.awt.Rectangle(
				rect.x1.toInt(),
				rect.y1.toInt(),
				(rect.x2 - rect.x1).toInt(),
				(rect.y2 - rect.y1).toInt()
		)

		/*String v = String.format("Draw.java Clip rect [%s] ", uiobject.getID());
		uiobject.getTopObject().debug.set(v + "rect", rect.toString());
		uiobject.getTopObject().debug.set(v + "r", r.toString());*/

		g2d.clip(r)
	}

	private fun unclip() {
		g2d.clip = null
	}

	fun disableOutline() {
		skip_outline = true
	}

	fun enableOutline() {
		skip_outline = false
	}

	/*
	 * Shows debug for the current UIObject
	 */
	fun debug() {
		val prev = skip_outline
		skip_outline = true
		if (outline != null) {
			setColor(255, 255, 0)
			this.setStroke(0.5f)
			rect(outline!!.x1, outline!!.y1, outline!!.x2 - outline!!.x1, outline!!.y2 - outline!!.y1)
		}

		if (uiobject.translation.clip != null) {
			val c = uiobject.translation.clip
			setColor(0, 0, 255)
			rect(c.x1, c.y1, c.x2 - c.x1, c.y2 - c.y1)
		}

		skip_outline = prev
	}
}
