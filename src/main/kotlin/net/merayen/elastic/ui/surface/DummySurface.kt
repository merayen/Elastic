package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.ImmutableDimension
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.util.ImmutablePoint
import net.merayen.elastic.util.MutablePoint
import java.awt.*
import java.awt.font.GlyphVector
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ImageObserver
import java.awt.image.RenderedImage
import java.awt.image.renderable.RenderableImage
import java.text.AttributedCharacterIterator
import kotlin.concurrent.timer

internal class DummyGraphics2D : Graphics2D() { // This smells Java
	override fun getClipBounds() = TODO()
	override fun drawPolyline(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) = TODO()
	override fun rotate(theta: Double) = TODO()
	override fun rotate(theta: Double, x: Double, y: Double) = TODO()
	override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {}
	override fun copyArea(x: Int, y: Int, width: Int, height: Int, dx: Int, dy: Int) = TODO()
	override fun draw(s: Shape?) = TODO()
	override fun setStroke(s: Stroke?) {}
	override fun getComposite() = TODO()
	override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) = TODO()
	override fun fill(s: Shape?) = TODO()
	override fun getDeviceConfiguration() = TODO()
	override fun getBackground() = TODO()
	override fun clip(s: Shape?) {}
	override fun setPaint(paint: Paint?) = TODO()
	override fun drawString(str: String, x: Int, y: Int) = TODO()
	override fun drawString(str: String?, x: Float, y: Float) = TODO()
	override fun drawString(iterator: AttributedCharacterIterator?, x: Int, y: Int) = TODO()
	override fun drawString(iterator: AttributedCharacterIterator?, x: Float, y: Float) = TODO()
	override fun clipRect(x: Int, y: Int, width: Int, height: Int) = TODO()
	override fun shear(shx: Double, shy: Double) = TODO()
	override fun transform(Tx: AffineTransform?) = TODO()
	override fun setPaintMode() = TODO()
	override fun getColor() = TODO()
	override fun scale(sx: Double, sy: Double) = TODO()
	override fun drawImage(img: Image?, xform: AffineTransform?, obs: ImageObserver?) = TODO()
	override fun drawImage(img: BufferedImage?, op: BufferedImageOp?, x: Int, y: Int) = TODO()
	override fun drawImage(img: Image?, x: Int, y: Int, observer: ImageObserver?) = TODO()
	override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, observer: ImageObserver?) = TODO()
	override fun drawImage(img: Image?, x: Int, y: Int, bgcolor: Color?, observer: ImageObserver?) = TODO()
	override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, bgcolor: Color?, observer: ImageObserver?) = TODO()
	override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, observer: ImageObserver?) = TODO()
	override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, bgcolor: Color?, observer: ImageObserver?) = TODO()
	override fun getFontRenderContext() = TODO()
	override fun setXORMode(c1: Color?) = TODO()
	override fun addRenderingHints(hints: MutableMap<*, *>?) = TODO()
	override fun getRenderingHints() = TODO()
	override fun translate(x: Int, y: Int) = TODO()
	override fun translate(tx: Double, ty: Double) = TODO()
	override fun setFont(font: Font?) {}
	override fun getFont() = TODO()
	override fun getStroke() = TODO()
	override fun fillOval(x: Int, y: Int, width: Int, height: Int) {}
	override fun getClip() = TODO()
	override fun drawRenderedImage(img: RenderedImage?, xform: AffineTransform?) = TODO()
	override fun dispose() = TODO()
	override fun setClip(x: Int, y: Int, width: Int, height: Int) = TODO()
	override fun setClip(clip: Shape?) {}
	override fun setRenderingHints(hints: MutableMap<*, *>?) = TODO()
	override fun getTransform() = TODO()
	override fun create() = TODO()
	override fun drawOval(x: Int, y: Int, width: Int, height: Int) = TODO()
	override fun drawRenderableImage(img: RenderableImage?, xform: AffineTransform?) = TODO()
	override fun setComposite(comp: Composite?) = TODO()
	override fun clearRect(x: Int, y: Int, width: Int, height: Int) = TODO()
	override fun drawPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) = TODO()
	override fun setTransform(Tx: AffineTransform?) = TODO()
	override fun getPaint() = TODO()
	override fun fillRect(x: Int, y: Int, width: Int, height: Int) {}
	override fun drawGlyphVector(g: GlyphVector?, x: Float, y: Float) = TODO()
	override fun drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) = TODO()
	override fun getFontMetrics(f: Font?) = TODO()
	override fun fillPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) = TODO()
	override fun setColor(c: Color?) = TODO()
	override fun setRenderingHint(hintKey: RenderingHints.Key?, hintValue: Any?) = TODO()
	override fun fillRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) = TODO()
	override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) = TODO()
	override fun getRenderingHint(hintKey: RenderingHints.Key?) = TODO()
	override fun hit(rect: Rectangle?, s: Shape?, onStroke: Boolean) = TODO()
	override fun setBackground(color: Color?) = TODO()
}

internal class DummySurface(id: String, handler: Surface.Handler) : Surface(id, handler) {
	override val threadId: Long
		get() = Thread.currentThread().id  // We do not care about threading with this dummy

	override val surfaceLocation = MutablePoint()

	private val timer = timer(period = 1000 / 60, action = { handler.onDraw(DummyGraphics2D()) })

	private var running = true


	override val nativeUI = object : NativeUI {
		override val screen = object : NativeUI.Screen {
			override val activeScreenSize: ImmutableDimension
				get() = ImmutableDimension(1000f, 1000f)
		}
		override val window = object : NativeUI.Window {
			override var position: ImmutablePoint
				get() = ImmutablePoint(0f, 0f)
				set(_) {}
			override var size: ImmutableDimension
				get() = ImmutableDimension(1000f, 1000f)
				set(_) {}
			override var isDecorated = true
		}
		override val mouseCursor = object : NativeUI.MouseCursor {
			override fun setPosition(point: MutablePoint) {}
			override fun getPosition() = MutablePoint()
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