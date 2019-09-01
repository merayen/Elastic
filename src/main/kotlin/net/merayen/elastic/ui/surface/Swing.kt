package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.event.*
import net.merayen.elastic.util.AverageStat
import net.merayen.elastic.util.Point
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.File
import java.io.IOException
import java.util.*
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
/**
 * Java Swing Surface.
 * TODO move somewhere else?
 */
class Swing(id: String, handler: Handler) : Surface(id, handler) {
	private lateinit var panel: LolPanel
	private var frame: LolFrame? = null
	private val eventsQueue = ArrayList<UIEvent>()

	private var internalThreadId: Long? = null

	override val threadId: Long
		get() = internalThreadId ?: throw RuntimeException("UI thread id not detected yet")

	/*
	 * A surface to draw on for the Java Swing GUI.
	 */
	inner class LolFrame(private val close_function: Runnable) : javax.swing.JFrame(), java.awt.event.ActionListener {
		val timer = javax.swing.Timer(1000 / 60, this)

		init {
			val dp = DropTarget()

			try {
				dp.addDropTargetListener(object : DropTargetListener {
					override fun dragEnter(dtde: DropTargetDragEvent) {}
					override fun dragOver(dtde: DropTargetDragEvent) {}
					override fun dropActionChanged(dtde: DropTargetDragEvent) {}
					override fun dragExit(dte: DropTargetEvent) {}

					override fun drop(dtde: DropTargetDropEvent) {
						dtde.acceptDrop(dtde.dropAction)
						try {
							@Suppress("UNCHECKED_CAST")
							val fileInstances = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
							val files: List<String> = (fileInstances.map { it.absolutePath })

							queueEvent(FileDropEvent(dtde.dropTargetContext.component.name, 0, 0, 0, MouseEvent.Action.DROP, MouseEvent.Button.LEFT, files.toTypedArray())) // TODO figure out coordinates

							for (f in files)
								println("File dropped: $f")

						} catch (e: UnsupportedFlavorException) {
							e.printStackTrace()
						} catch (e: IOException) {
							e.printStackTrace()
						}
					}
				})
			} catch (lolWhatShouldIDo: Exception) {
				throw RuntimeException(lolWhatShouldIDo)
			}

			this.dropTarget = dp
			initUI()
			timer.start()
		}

		override fun actionPerformed(e: ActionEvent) = repaint()

		private fun initUI() {
			addWindowListener(object : java.awt.event.WindowAdapter() {
				override fun windowClosing(we: java.awt.event.WindowEvent?) {
					timer.stop()
					close_function.run()
				}
			})

			title = "Elastic"
			setSize(800, 800)
			defaultCloseOperation = EXIT_ON_CLOSE
			isVisible = true
		}

		fun end() {
			timer.stop()
		}
	}

	inner class LolPanel : javax.swing.JPanel(), java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener, java.awt.event.KeyListener {
		private val active_key_codes = HashSet<Int>() // Ugly hack as JKeyListener repeats the keys, at least for Linux

		private var bufferedImage: BufferedImage? = null
		private var bufferRendering = false

		init {
			isFocusable = true
			//grabFocus();
			addMouseListener(this)
			addMouseMotionListener(this)
			addMouseWheelListener(this)
			addKeyListener(this)
		}


		private var lol = 0
		private val averageStatLive = AverageStat<Long>(120)
		private val averageStatBuffered = AverageStat<Long>(120)
		public override fun paintComponent(g: java.awt.Graphics) {
			if (internalThreadId == null)
				internalThreadId = Thread.currentThread().id
			else if (internalThreadId != Thread.currentThread().id)
				throw RuntimeException("Called by another thread, not expected")

			//RenderingHints rh = new RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			//((java.awt.Graphics2D)g).setRenderingHints(rh);
			//super.paintComponent(g);

			//bufferRendering = (lol++ % 120) > 60
			if (bufferRendering) {
				val t = System.currentTimeMillis()

				if (bufferedImage?.width != width || bufferedImage?.height != height)
					this.bufferedImage = null

				val bufferedImage = bufferedImage ?: BufferedImage(width, height, IndexColorModel.BITMASK);
				handler.onDraw(bufferedImage.createGraphics()!!)

				g.drawImage(bufferedImage, 0, 0, bufferedImage.width, bufferedImage.height, null)

				averageStatBuffered.add(System.currentTimeMillis() - t)
			} else {
				val t = System.currentTimeMillis()
				handler.onDraw(g as Graphics2D)
				averageStatLive.add(System.currentTimeMillis() - t)
			}

			if (lol % 60 == 0) {
				//println("Buffered: ${averageStatBuffered.info()}   /   Live: ${averageStatLive.info()}")
			}
		}

		override fun mousePressed(e: java.awt.event.MouseEvent) {
			createMouseEvent(e, MouseEvent.Action.DOWN)
		}

		override fun mouseReleased(e: java.awt.event.MouseEvent) {
			createMouseEvent(e, MouseEvent.Action.UP)
		}

		override fun mouseEntered(e: java.awt.event.MouseEvent) {}
		override fun mouseExited(e: java.awt.event.MouseEvent) {
			createMouseEvent(e, MouseEvent.Action.OUT_OF_RANGE)
		}

		override fun mouseClicked(e: java.awt.event.MouseEvent) {}

		override fun mouseMoved(e: java.awt.event.MouseEvent) {
			createMouseEvent(e, MouseEvent.Action.MOVE)
		}

		override fun mouseDragged(e: java.awt.event.MouseEvent) {
			createMouseEvent(e, MouseEvent.Action.MOVE)
		}

		override fun mouseWheelMoved(e: java.awt.event.MouseWheelEvent) {
			queueEvent(MouseWheelEvent(e.component.name, 0, e.wheelRotation))
		}

		override fun keyTyped(e: KeyEvent) {}

		override fun keyPressed(e: KeyEvent) {
			if (!active_key_codes.contains(e.keyCode)) {
				active_key_codes.add(e.keyCode)
				queueEvent(KeyboardEvent(e.component.name, e.keyChar, e.keyCode, KeyboardEvent.Action.DOWN))
			}
		}

		override fun keyReleased(e: KeyEvent) {
			if (active_key_codes.contains(e.keyCode)) {
				active_key_codes.remove(e.keyCode)
				queueEvent(KeyboardEvent(e.component.name, e.keyChar, e.keyCode, KeyboardEvent.Action.UP))
			}
		}
	}

	init {
		EventQueue.invokeLater {
			val frame = LolFrame(Runnable { end() })
			panel = LolPanel()
			panel.name = id
			frame.add(panel)

			this.frame = frame
		}

		for(i in 0 until 100)
			if (frame == null)
				Thread.sleep(10)
			else
				break

		if (frame == null)
			throw RuntimeException("Failed to initialize frame in expected time window")
	}

	override val width: Int
		get() = panel.width

	override val height: Int
		get() = panel.height

	override val surfaceLocation: Point
		get() = Point(panel.locationOnScreen.x.toFloat(), this.panel.locationOnScreen.y.toFloat())


	private fun createMouseEvent(e: java.awt.event.MouseEvent, action: MouseEvent.Action) {
		var button: MouseEvent.Button? = null

		val b = e.button

		when (b) {
			java.awt.event.MouseEvent.BUTTON1 -> button = MouseEvent.Button.LEFT
			java.awt.event.MouseEvent.BUTTON2 -> button = MouseEvent.Button.MIDDLE
			java.awt.event.MouseEvent.BUTTON3 -> button = MouseEvent.Button.RIGHT
			// Mouse is always id 0. Gamepads will have an higher id
		}

		queueEvent(MouseEvent(e.component.name, 0, e.x, e.y, action, button)) // Mouse is always id 0. Gamepads will have an higher id
	}

	private fun queueEvent(event: UIEvent) {
		synchronized(eventsQueue) {
			eventsQueue.add(event)
		}
	}

	override fun end() {
		frame?.end()
		frame?.isVisible = false
		frame?.dispose()
		frame?.timer?.stop()

		frame = null
	}

	override fun pullEvents(): List<UIEvent> {
		var result: List<UIEvent>

		synchronized(eventsQueue) {
			result = ArrayList(eventsQueue)
			eventsQueue.clear()
		}

		return result
	}


	inner class SwingNativeUI : NativeUI {


		override val mouseCursor = object : NativeUI.MouseCursor {
			private val blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), java.awt.Point(), "blank cursor")

			override fun setPosition(point: Point) {
				SwingUtilities.invokeLater {
					Robot().mouseMove(point.x.toInt(), point.y.toInt())
				}
			}

			override fun getPosition(): Point {
				val location = MouseInfo.getPointerInfo().location
				return Point(location.getX().toFloat(), location.getY().toFloat())
			}

			override fun hide() {
				frame?.contentPane?.cursor = blankCursor
			}

			override fun show() {
				frame?.contentPane?.cursor = Cursor.getDefaultCursor()
			}
		}

		override val dialog = object : NativeUI.Dialog {
			override fun showTextInput(description: String, value: String, onDone: (value: String?) -> Unit) {
				SwingUtilities.invokeLater {
					onDone(JOptionPane.showInputDialog(description, value))
				}
			}
		}

	}

	override val nativeUI = SwingNativeUI()

	override fun isReady() = frame != null
}

