package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.system.intercom.NodeDisconnectMessage
import net.merayen.elastic.system.intercom.RemoveNodeMessage
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch
import net.merayen.elastic.ui.objects.top.views.nodeview.inlinewindows.AddNodeInlineWindow
import net.merayen.elastic.ui.objects.top.views.nodeview.inlinewindows.FindNodeInlineWindow
import net.merayen.elastic.ui.objects.top.marks.MarksInlineWindow
import net.merayen.elastic.ui.util.ArrowNavigation
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.util.NodeUtil
import kotlin.math.roundToInt

class NodeViewEasyMotion(private val nodeView: NodeView) {
	private var addNodeWindow: AddNodeInlineWindow? = null
	private var findNodeWindow: FindNodeInlineWindow? = null
	private var marksWindow: MarksInlineWindow? = null

	private val navigation = NodeViewNavigation(nodeView)

	private val marks = HashMap<Char, String>()

	init {
		nodeView.add(navigation)
	}

	fun createEasyMotionBranch() = object : Branch(nodeView) {
		init {
			controls[setOf(KeyboardEvent.Keys.F)] = Control {
				showFindNode()
			}

			controls[setOf(KeyboardEvent.Keys.Q)] = Control { Control.STEP_BACK }

			// Navigation
			controls[setOf(KeyboardEvent.Keys.LEFT)] = Control {
				navigation.move(ArrowNavigation.Direction.LEFT)
				null
			}

			controls[setOf(KeyboardEvent.Keys.RIGHT)] = Control {
				navigation.move(ArrowNavigation.Direction.RIGHT)
				null
			}

			controls[setOf(KeyboardEvent.Keys.UP)] = Control {
				navigation.move(ArrowNavigation.Direction.UP)
				null
			}

			controls[setOf(KeyboardEvent.Keys.DOWN)] = Control {
				navigation.move(ArrowNavigation.Direction.DOWN)
				null
			}

			controls[setOf(KeyboardEvent.Keys.D)] = Control {
				val c = navigation.current
				when (c) {
					is UIPort -> println("port (but ignored, because no)")
					is UINode -> nodeView.sendMessage(RemoveNodeMessage(c.nodeId))
					is NodeViewNavigation.Line -> nodeView.sendMessage(NodeDisconnectMessage(c.portA.node.nodeId, c.portA.name, c.portB.node.nodeId, c.portB.name))
					else -> println("Nothing to delete")
				}
				null
			}

			controls[setOf(KeyboardEvent.Keys.A)] = Control {
				val c = navigation.current
				when (c) {
					is UIPort -> {
						showAddNode()
					}
					is UINode -> {
						println("Supposed to just add a node close to current node")
						showAddNode()
					}
					is NodeViewNavigation.Line -> {
						println("Supposed to open the Create Node-window and try to connect that node into that line")
						showAddNode()
					}
					else -> showAddNode()
				}
			}

			controls[setOf(KeyboardEvent.Keys.ENTER)] = Control {
				val c = navigation.current
				if (c is UINode) println("Supposed to go into node for editing its parameters")
				null
			}

			controls[setOf(KeyboardEvent.Keys.Y)] = Control {
				when (navigation.current) {
					is UINode -> println("Supposed to copy node. This should copy the node and its data into memory")
				}
				null
			}

			controls[setOf(KeyboardEvent.Keys.P)] = Control {
				val c = navigation.current
				when (c) {
					is UINode -> println("Supposed to paste node on the right of selected node")
					is UIPort -> println("Supposed to paste node and try to connect pasted node to selected port")
					is NodeViewNavigation.Line -> print("Supposed to paste node and try to insert it between the connected nodes")
				}
				null
			}

			controls[setOf(KeyboardEvent.Keys.C)] = Control {
				val c = navigation.current
				when (c) {
					is UIPort -> {
						println("Supposed to open some kind of connect-wizard, listing all connectable ports, based on data types and recommendation, and close to the port already")
						showFindNode(c)
					}
				}
				null
			}

			controls[setOf(KeyboardEvent.Keys.M)] = Control {
				val c = navigation.current
				when (c) {
					is UINode -> showMarksWindow(MarksInlineWindow.Mode.SET)
					else -> null
				}
			}

			controls[setOf(KeyboardEvent.Keys.APOSTROPHE)] = Control {
				showMarksWindow(MarksInlineWindow.Mode.GOTO)
			}

			controls[setOf(KeyboardEvent.Keys.MINUS)] = Control {
				nodeView.container.zoomScaleXTarget *= 1.2f
				nodeView.container.zoomScaleYTarget *= 1.2f
				nodeView.container.translateXTarget *= 1.4f
				nodeView.container.translateYTarget *= 1.4f
				null
			}

			controls[setOf(KeyboardEvent.Keys.PLUS)] = Control {
				nodeView.container.zoomScaleXTarget /= 1.2f
				nodeView.container.zoomScaleYTarget /= 1.2f
				nodeView.container.translateXTarget /= 1.4f
				nodeView.container.translateYTarget /= 1.4f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.LEFT)] = Control {
				nodeView.container.translateXTarget += 100f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.RIGHT)] = Control {
				nodeView.container.translateXTarget -= 100f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.UP)] = Control {
				nodeView.container.translateYTarget += 100f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.DOWN)] = Control {
				nodeView.container.translateYTarget -= 100f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.LEFT)] = Control {
				nodeView.container.translateXTarget += 500f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.RIGHT)] = Control {
				nodeView.container.translateXTarget -= 500f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.UP)] = Control {
				nodeView.container.translateYTarget += 500f
				null
			}

			controls[setOf(KeyboardEvent.Keys.CONTROL, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.DOWN)] = Control {
				nodeView.container.translateYTarget -= 500f
				null
			}

			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				navigation.current = null
				null
			}

			controls[setOf(KeyboardEvent.Keys.U)] = Control {
				println("Supposed to undo last change")
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.UP)] = Control { // Should perhaps instead be a
				moveNode(0f, -20f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.DOWN)] = Control { // Should perhaps instead be a
				moveNode(0f, 20f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.LEFT)] = Control { // Should perhaps instead be a
				moveNode(-20f, 0f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.RIGHT)] = Control { // Should perhaps instead be a
				moveNode(20f, 0f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.UP)] = Control { // Should perhaps instead be a
				moveNode(0f, -100f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.DOWN)] = Control { // Should perhaps instead be a
				moveNode(0f, 100f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.LEFT)] = Control { // Should perhaps instead be a
				moveNode(-100f, 0f)
				null
			}

			controls[setOf(KeyboardEvent.Keys.ALT, KeyboardEvent.Keys.SHIFT, KeyboardEvent.Keys.RIGHT)] = Control { // Should perhaps instead be a
				moveNode(100f, 0f)
				null
			}
		}
	}

	private fun moveNode(x: Float, y: Float) {
		val c = navigation.current
		if (c is UINode) {
			c.targetLocation.x += x
			c.targetLocation.y += y

			// Quantize
			c.targetLocation.x = ((c.targetLocation.x / 20).roundToInt() * 20).toFloat()
			c.targetLocation.y = ((c.targetLocation.y / 20).roundToInt() * 20).toFloat()

			c.sendUiData()
		}
	}

	private fun showAddNode(): EasyMotionBranch {
		val addNodeWindow = addNodeWindow
		if (addNodeWindow == null) {
			val newWindow = AddNodeInlineWindow()
			newWindow.handler = object : AddNodeInlineWindow.Handler {
				override fun onClose() {
					if (newWindow.parent != null)
						nodeView.remove(newWindow)
					this@NodeViewEasyMotion.addNodeWindow = null
				}

				override fun onSelect(node: BaseInfo) {
					val nodeViewNodeId = nodeView.currentNodeId ?: throw RuntimeException("Should not happen")

					// Grr, probably put this somewhere
					val path = node.javaClass.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
					val name = path[path.size - 2]
					val nodeName = NodeUtil.getNodeName(name)
					val nodeVersion = NodeUtil.getNodeVersion(name)

					val nodeId = NodeUtil.createID()

					nodeView.sendMessage(
						CreateNodeMessage(
							nodeId,
							nodeName,
							nodeVersion,
							nodeViewNodeId
						)
					)

					// Automatically connect the node when it gets created
					val current = navigation.current
					if (current is UIPort) {
						val start = System.currentTimeMillis() + 1000
						nodeView.taskQueue.add {
							if (start > System.currentTimeMillis()) {
								val newNode = nodeView.nodes[nodeId]

								if (newNode != null) {
									if (current.output) {
										val newNodeInputPort = newNode.ports.firstOrNull { !it.output }
										if (newNodeInputPort != null)
											nodeView.sendMessage(NodeConnectMessage(current.uinode.nodeId, current.name, newNode.nodeId, newNodeInputPort.name))

										true
									} else {
										val newNodeOutputPort = newNode.ports.firstOrNull { it.output }
										if (newNodeOutputPort != null)
											nodeView.sendMessage(NodeConnectMessage(current.uinode.nodeId, current.name, newNode.nodeId, newNodeOutputPort.name))

										true
									}
								} else {
									println("Waiting for node to get created...")

									false // Waiting for node to get created
								}
							} else {
								true // Timed out
							}
						}
					}

					// Try to focus the UINode when it gets actually created
					val timeout = System.currentTimeMillis() + 1000
					nodeView.taskQueue.add {
						if (timeout > System.currentTimeMillis()) {
							val uinode = nodeView.nodes[nodeId]
							if (uinode != null) {
								nodeView.focus(uinode)
								true // Success
							} else {
								false // Try again later
							}
						} else {
							false // Timed out
						}
					}

					newWindow.close()

				}
			}

			newWindow.translation.y = 20f
			nodeView.add(newWindow)

			this.addNodeWindow = newWindow

			return newWindow.filterInlineWindow
		} else {
			return addNodeWindow.filterInlineWindow
		}
	}

	private fun showFindNode(connectingPort: UIPort? = null): EasyMotionBranch {
		val findNodeWindow = findNodeWindow

		if (findNodeWindow == null) {
			val newWindow = FindNodeInlineWindow(nodeView.nodeViewController!!.netList)
			newWindow.handler = object : FindNodeInlineWindow.Handler {
				override fun onSelect(nodeId: String) {
					// TODO set correct node parent id on NodeView, then move to the node
					newWindow.close()
					if (connectingPort != null) {
						println("Supposed to show a list of selected node's ports that we can connect to? Filtered on data types?")
					}
				}

				override fun onFocus(nodeId: String) {
					val uinode = nodeView.nodes[nodeId] ?: return
					nodeView.focus(uinode)
					navigation.current = uinode
				}

				override fun onClose() {
					if (newWindow.parent != null)
						nodeView.remove(newWindow)
					this@NodeViewEasyMotion.findNodeWindow = null
				}

			}

			newWindow.translation.x = 40f
			newWindow.translation.y = 20f
			nodeView.add(newWindow)

			this.findNodeWindow = newWindow

			return newWindow.filterInlineWindow
		} else {
			return findNodeWindow.filterInlineWindow
		}
	}

	private fun showMarksWindow(mode: MarksInlineWindow.Mode): EasyMotionBranch {
		val marksWindow = marksWindow
		if (marksWindow == null) {
			val newWindow = MarksInlineWindow(mode)
			newWindow.handler = object : MarksInlineWindow.Handler {
				override fun onSelect(mark: Char) {
					println("Du har valgt å sette marker på $mark")
				}

				override fun onClose() {
					if (newWindow.parent != null)
						nodeView.remove(newWindow)
					this@NodeViewEasyMotion.marksWindow = null
				}
			}

			newWindow.translation.x = 40f
			newWindow.translation.y = 40f
			nodeView.add(newWindow)

			this@NodeViewEasyMotion.marksWindow = newWindow

			return newWindow
		} else {
			return marksWindow
		}
	}
}
