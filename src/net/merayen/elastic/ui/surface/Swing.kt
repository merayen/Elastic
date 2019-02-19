package net.merayen.elastic.ui.surface

import net.merayen.elastic.ui.event.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashSet

/**
 * Java Swing Surface.
 * TODO move somewhere else?
 */
class Swing(id: String, handler: Surface.Handler) : Surface(id, handler) {
	private lateinit var panel: LolPanel
	private lateinit var frame: LolFrame
	private val eventsQueue = ArrayList<UIEvent>()

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
			defaultCloseOperation = javax.swing.JFrame.EXIT_ON_CLOSE
			isVisible = true
		}

		fun end() {
			timer.stop()
		}
	}

	inner class LolPanel : javax.swing.JPanel(), java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener, java.awt.event.KeyListener {
		private val active_key_codes = HashSet<Int>() // Ugly hack as JKeyListener repeats the keys, at least for Linux

		init {
			isFocusable = true
			//grabFocus();
			addMouseListener(this)
			addMouseMotionListener(this)
			addMouseWheelListener(this)
			addKeyListener(this)
		}

		public override fun paintComponent(g: java.awt.Graphics) {
			//RenderingHints rh = new RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			//((java.awt.Graphics2D)g).setRenderingHints(rh);
			//super.paintComponent(g);

			handler.onDraw(g as java.awt.Graphics2D)
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
		java.awt.EventQueue.invokeLater {
			frame = LolFrame(Runnable { end() })
			panel = LolPanel()
			panel.name = id
			frame.add(panel)
		}
	}

	override fun getWidth(): Int {
		return this.panel.width
	}

	override fun getHeight(): Int {
		return this.panel.height
	}

	private fun createMouseEvent(e: java.awt.event.MouseEvent, action: MouseEvent.Action) {
		var button: MouseEvent.Button? = null

		val b = e.button

		if (b == java.awt.event.MouseEvent.BUTTON1)
			button = MouseEvent.Button.LEFT
		else if (b == java.awt.event.MouseEvent.BUTTON2)
			button = MouseEvent.Button.MIDDLE
		else if (b == java.awt.event.MouseEvent.BUTTON3)
			button = MouseEvent.Button.RIGHT

		queueEvent(MouseEvent(e.component.name, 0, e.x, e.y, action, button)) // Mouse is always id 0. Gamepads will have an higher id
	}

	private fun queueEvent(event: UIEvent) {
		synchronized(eventsQueue) {
			eventsQueue.add(event)
		}
	}

	override fun end() {
		frame.isVisible = false
		frame.dispose()
		frame.timer.stop()
	}

	override fun pullEvents(): List<UIEvent> {
		var result: List<UIEvent>

		synchronized(eventsQueue) {
			result = ArrayList(eventsQueue)
			eventsQueue.clear()
		}

		return result
	}
}
