package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.ImmutableDimension
import net.merayen.elastic.ui.event.*
import net.merayen.elastic.util.AverageStat
import net.merayen.elastic.util.ImmutablePoint
import net.merayen.elastic.util.MutablePoint
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
	private var panel: LolPanel? = null
	private var frame: LolFrame? = null
	private val eventsQueue = ArrayList<UIEvent>()

	/**
	 * The dimension of the panel (not the frame/window).
	 */
	private var dimension = Dimension()

	private val this_Swing = this

	private var isDecorated = true

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
			defaultCloseOperation = EXIT_ON_CLOSE
			isUndecorated = !isDecorated
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
			preferredSize = Dimension(dimension.width, dimension.height)

			addMouseListener(this)
			addMouseMotionListener(this)
			addMouseWheelListener(this)
			addKeyListener(this)
		}

		private val averageStatLive = AverageStat<Long>(120)
		private val averageStatBuffered = AverageStat<Long>(120)
		public override fun paintComponent(graphics: Graphics) {
			if (internalThreadId == null)
				internalThreadId = Thread.currentThread().id
			else if (internalThreadId != Thread.currentThread().id)
				throw RuntimeException("Called by another thread, not expected")

			graphics as Graphics2D
			val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
			//(graphics as Graphics2D).setRenderingHints(rh);

			if (bufferRendering) {
				val t = System.currentTimeMillis()

				if (bufferedImage?.width != width || bufferedImage?.height != height)
					this.bufferedImage = null

				val bufferedImage = bufferedImage ?: BufferedImage(width, height, IndexColorModel.BITMASK)

				handler.onDraw(bufferedImage.createGraphics()!!)

				graphics.drawImage(bufferedImage, 0, 0, bufferedImage.width, bufferedImage.height, null)

				averageStatBuffered.add(System.currentTimeMillis() - t)
			} else {
				val t = System.currentTimeMillis()
				handler.onDraw(graphics)
				averageStatLive.add(System.currentTimeMillis() - t)
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
				queueEvent(KeyboardEvent(e.component.name, e.keyChar, e.keyCode, true))
			}
		}

		override fun keyReleased(e: KeyEvent) {
			if (active_key_codes.contains(e.keyCode)) {
				active_key_codes.remove(e.keyCode)
				queueEvent(KeyboardEvent(e.component.name, e.keyChar, e.keyCode, false))
			}
		}
	}

	@Volatile
	private var creatingWindowState = false

	/**
	 * Destroys current window if any, and creates a new one.
	 */
	@Synchronized
	private fun createWindow() {
		if (creatingWindowState)
			throw RuntimeException("Already creating window")

		try {
			creatingWindowState = true

			end()

			if (SwingUtilities.isEventDispatchThread()) {
				createPanels()
			} else {
				EventQueue.invokeLater {
					createPanels()
				}

				// Block and wait for window being created
				for (i in 0 until 1000)
					if (creatingWindowState)
						Thread.sleep(10)
					else
						break
			}
			if (creatingWindowState)
				throw RuntimeException("Failed to initialize frame in expected time window")
		} finally {
			creatingWindowState = false
		}
	}

	private fun createPanels() {
		val frame = LolFrame(Runnable { end() })
		val panel = LolPanel()
		panel.name = id
		frame.add(panel)
		frame.pack()

		this.frame = frame
		this.panel = panel

		creatingWindowState = false
	}

	override fun end() {
		frame?.end()
		frame?.isVisible = false
		frame?.dispose()
		frame?.timer?.stop()

		frame = null
		panel = null
	}

	init {
		createWindow()
	}

	@Deprecated("Use NativeUI instead?")
	override val surfaceLocation: MutablePoint
		get() = MutablePoint(panel?.locationOnScreen?.x?.toFloat() ?: 0f, panel?.locationOnScreen?.y?.toFloat() ?: 0f)


	private fun createMouseEvent(e: java.awt.event.MouseEvent, action: MouseEvent.Action) {
		var button: MouseEvent.Button? = null

		when (e.button) {
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

			override fun setPosition(point: MutablePoint) {
				SwingUtilities.invokeLater {
					Robot().mouseMove(point.x.toInt(), point.y.toInt())
				}
			}

			override fun getPosition(): MutablePoint {
				val location = MouseInfo.getPointerInfo().location
				return MutablePoint(location.getX().toFloat(), location.getY().toFloat())
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

		override val screen = object : NativeUI.Screen {
			override val activeScreenSize: ImmutableDimension
				get() {
					val screenSize = Toolkit.getDefaultToolkit().screenSize
					return ImmutableDimension(screenSize.width.toFloat(), screenSize.height.toFloat())
				}
		}

		override val window = object : NativeUI.Window {
			override var position: ImmutablePoint
				get() = ImmutablePoint(surfaceLocation)
				set(value) {
					frame!!.setLocation(value.x.toInt(), value.y.toInt())
				}

			override var size: ImmutableDimension
				get() = ImmutableDimension(panel?.width?.toFloat() ?: 0f, panel?.height?.toFloat() ?: 0f)
				set(value) {
					dimension.width = value.width.toInt()
					dimension.height = value.height.toInt()

					panel!!.preferredSize = Dimension(value.width.toInt(), value.height.toInt())
					frame!!.pack()
				}

			override var isDecorated: Boolean
				get() = this_Swing.isDecorated
				set(value) {
					if (value != this_Swing.isDecorated) {
						// Swing requires us to create a new window to change the decoration
						this_Swing.isDecorated = value
						createWindow()
					}
				}
		}
	}

	override val nativeUI = SwingNativeUI()

	override fun isReady() = frame != null
}