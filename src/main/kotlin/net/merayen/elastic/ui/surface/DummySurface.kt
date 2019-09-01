package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.util.Point
import java.awt.*
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ImageObserver
import java.awt.image.RenderedImage
import java.awt.image.renderable.RenderableImage
import java.text.AttributedCharacterIterator
import kotlin.concurrent.timer

internal class DummyGraphics2D : Graphics2D() {
	override fun getClipBounds(): Rectangle {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawPolyline(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun rotate(theta: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun rotate(theta: Double, x: Double, y: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {}

	override fun copyArea(x: Int, y: Int, width: Int, height: Int, dx: Int, dy: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun draw(s: Shape?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setStroke(s: Stroke?) {}

	override fun getComposite(): Composite {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fill(s: Shape?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getDeviceConfiguration(): GraphicsConfiguration {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getBackground(): Color {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun clip(s: Shape?) {}

	override fun setPaint(paint: Paint?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawString(str: String, x: Int, y: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawString(str: String?, x: Float, y: Float) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawString(iterator: AttributedCharacterIterator?, x: Int, y: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawString(iterator: AttributedCharacterIterator?, x: Float, y: Float) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun clipRect(x: Int, y: Int, width: Int, height: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun shear(shx: Double, shy: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun transform(Tx: AffineTransform?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setPaintMode() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getColor(): Color {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun scale(sx: Double, sy: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, xform: AffineTransform?, obs: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: BufferedImage?, op: BufferedImageOp?, x: Int, y: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, x: Int, y: Int, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, x: Int, y: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getFontRenderContext(): FontRenderContext {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setXORMode(c1: Color?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun addRenderingHints(hints: MutableMap<*, *>?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getRenderingHints(): RenderingHints {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun translate(x: Int, y: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun translate(tx: Double, ty: Double) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setFont(font: Font?) {}

	override fun getFont(): Font {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getStroke(): Stroke {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fillOval(x: Int, y: Int, width: Int, height: Int) {}

	override fun getClip(): Shape {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawRenderedImage(img: RenderedImage?, xform: AffineTransform?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun dispose() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setClip(x: Int, y: Int, width: Int, height: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setClip(clip: Shape?) {}

	override fun setRenderingHints(hints: MutableMap<*, *>?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getTransform(): AffineTransform {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun create(): Graphics {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawOval(x: Int, y: Int, width: Int, height: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawRenderableImage(img: RenderableImage?, xform: AffineTransform?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setComposite(comp: Composite?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun clearRect(x: Int, y: Int, width: Int, height: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setTransform(Tx: AffineTransform?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getPaint(): Paint {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fillRect(x: Int, y: Int, width: Int, height: Int) {}

	override fun drawGlyphVector(g: GlyphVector?, x: Float, y: Float) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getFontMetrics(f: Font?): FontMetrics {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fillPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setColor(c: Color?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setRenderingHint(hintKey: RenderingHints.Key?, hintValue: Any?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun fillRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getRenderingHint(hintKey: RenderingHints.Key?): Any {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun hit(rect: Rectangle?, s: Shape?, onStroke: Boolean): Boolean {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setBackground(color: Color?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}

internal class DummySurface(id: String, handler: Surface.Handler) : Surface(id, handler) {
	override val threadId: Long
		get() = Thread.currentThread().id  // We do not care about threading with this dummy

	override val width = 1000
	override val height = 1000

	override val surfaceLocation = Point()

	private val timer = timer(period = 1000/60, action = { handler.onDraw(DummyGraphics2D()) })

	private var running = true


	override val nativeUI = object : NativeUI {
		override val mouseCursor = object : NativeUI.MouseCursor {
			override fun setPosition(point: Point) {}
			override fun getPosition() = Point()
			override fun hide() {}
			override fun show() {}

		}

		override val dialog = object : NativeUI.Dialog {
			override fun showTextInput(description: String, value: String, onDone: (value: String?) -> Unit) {}
		}
	}

	override fun pullEvents(): List<UIEvent> = emptyList()

	override fun end() {
		timer.cancel()
		running = false
	}

	override fun isReady() = running
}