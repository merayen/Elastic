package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.nodeview.find.AddNodeWindow
import net.merayen.elastic.ui.util.ArrowNavigation
import net.merayen.elastic.uinodes.BaseInfo

class NodeViewEasyMotion(private val nodeView: NodeView) {
	private var addNodeWindow: AddNodeWindow? = null
	private val navigation = NodeViewNavigation(nodeView)

	init {
		nodeView.add(navigation)
	}

	fun createEasyMotionBranch() = object : Branch(nodeView) {
		init {
			controls[setOf(KeyboardEvent.Keys.F)] = Control {
				println("Supposed to show add-node-view in NodeView")
				showAddNode()
			}

			controls[setOf(KeyboardEvent.Keys.A)] = Control { println("Supposed to show a search box to find a node"); null }
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

			controls[setOf(KeyboardEvent.Keys.X)] = Control {
				println("Supposed to delete a ${when (navigation.current) {
					is UIPort -> "port (but ignored, because no)"
					is UINode -> "node"
					is NodeViewNavigation.Line -> "line"
					else -> "no idea"
				}}")
				null
			}

			controls[setOf(KeyboardEvent.Keys.A)] = Control {
				println(when (navigation.current) {
					is UIPort -> "Supposed to open the Create Node-window at this port"
					is UINode -> "Supposed to just add a node close to current node"
					is NodeViewNavigation.Line -> "Supposed to open the Create Node-window and try to connect that node into that line"
					else -> "Not sure what to do"
				})
				null
			}

			controls[setOf(KeyboardEvent.Keys.S)] = Control {
				println("Supposed to open search window that searches by node name, and nicknames")
				null
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
					is UIPort -> println("Supposed to open some kind of connect-wizard, listing all connectable ports, based on data types and recommendation, and close to the port already")
				}
				null
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

			controls[setOf(KeyboardEvent.Keys.ESCAPE)] = Control {
				navigation.current = null
				null
			}

			controls[setOf(KeyboardEvent.Keys.U)] = Control {
				println("Supposed to undo last change")
				null
			}
		}
	}

	private fun showAddNode(): AddNodeWindow {
		val findNodeWindow = addNodeWindow
		if (findNodeWindow == null) {
			val newWindow = AddNodeWindow()
			newWindow.handler = object : AddNodeWindow.Handler {
				override fun onClose() {
					if (newWindow.parent != null) {
						nodeView.remove(newWindow)
						addNodeWindow = null
					}
				}

				override fun onSelect(node: BaseInfo) {
					println("Du valgte ${node.name}!")
				}
			}

			newWindow.translation.y = 20f
			nodeView.add(newWindow)

			this.addNodeWindow = newWindow

			return newWindow
		} else {
			return findNodeWindow
		}
	}
}
