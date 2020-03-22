package net.merayen.elastic.ui.objects.top.views.nodeview


import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.util.ArrowNavigation
import net.merayen.elastic.util.Point

/**
 * Add-on for NodeView, giving feature for navigating the nodes by using arrow keys.
 * Navigation moves over UINode and UIPort in NodeView.
 *
 *                                       |-------------|
 *                                       | Node B      |
 *                                       |             |
 *                                       |             |
 *    ---------------                    |             |      |-------------|
 *    | Node A      |(1)_____ (2)_____(3)|             |   (4)| Node C      |
 *    |             | |     \  |         |-------------|    | |             |
 *    |             |(1)__…  \(2)__________________________(4)|             |
 *    |-------------|                                         |-------------|
 */
class NodeViewNavigation(private val nodeView: NodeView) : UIObject() {
	class Line(val portA: UIPort, val portB: UIPort)

	private val arrowNavigation = ArrowNavigation()

	private val uinodeMap = HashMap<UINode, ArrowNavigation.Point>()
	private val uiportMap = HashMap<UIPort, ArrowNavigation.Point>()
	private val lineMap = HashMap<Line, ArrowNavigation.Point>()
	private val pointMap = HashMap<ArrowNavigation.Point, Any>()

	private var dirty = true

	private var nodeViewRevision = -1
	private var uiNetRevision = -1

	override fun onDraw(draw: Draw) {
		val point = arrowNavigation.current ?: return

		when (pointMap[point]) {
			is UINode -> {
			}
			is UIPort -> {
			}
			is Line -> {
			}
			else -> throw RuntimeException("Should not happen")
		}

		val uinode = uinodeMap.entries.firstOrNull { it.value === point }?.key
		val uiport = uiportMap.entries.firstOrNull { it.value === point }?.key
		val line = lineMap.entries.firstOrNull { it.value === point }?.key

		val pos: Point
		val size: Point

		when {
			uinode != null -> {
				pos = getRelativePosition(uinode) ?: return
				size = getRelativePosition(uinode, uinode.getWidth(), uinode.getHeight()) ?: return
			}
			uiport != null -> {
				pos = getRelativePosition(uiport) ?: return
			}
			line != null -> {
				pos = getRelativePosition(line) ?: return
			}
		}

		draw.disableOutline()
		draw.setColor(1f, 1f, 0f)
		draw.setStroke(2f)
		draw.rect(pos.x - 5, pos.y - 5, current.getWidth() + 10, current.getHeight() + 10)
	}

	/**
	 * Everytime something has been changed, remember to call this method!
	 */
	fun rebuild() {
		uinodeMap.clear()
		uiportMap.clear()
		lineMap.clear()
		arrowNavigation.clear()

		// Add nodes
		for (uinode in nodeView.nodes.values) {
			val nodePoint = arrowNavigation.newPoint()
			pointMap[nodePoint] = uinode
			uinodeMap[uinode] = nodePoint

			// Add its ports and tie them to the node, so arrow keys can jump from node to ports
			for (uiport in uinode.getPorts()) {
				val uiportPoint = arrowNavigation.newPoint()
				uiportMap[uiport] = uiportPoint
				pointMap[uiportPoint] = uiport

				if (uiport.output) { // Right side on the node
					val nodePointRight = nodePoint.right

					uiportPoint.left = nodePoint // Goes from output-port back to node

					if (nodePointRight == null) { // No port added yet. We add the first one
						nodePoint.right = uiportPoint
					} else { // There are already a port, attach it below the previous port
						val lastNodePointRight = nodePointRight.findLast(ArrowNavigation.Direction.DOWN)
						lastNodePointRight.down = uiportPoint // Goes from output port to port below
						uiportPoint.up = lastNodePointRight // Goes from this output port to previous one, above
					}
				} else { // Left side on the node
					val nodePointLeft = nodePoint.left

					uiportPoint.right = nodePoint // Goes from input-port back to node

					if (nodePointLeft == null) {
						nodePoint.left = uiportPoint
					} else { // There are already a port, attach it below the previous port
						val lastNodePointLeft = nodePointLeft.findLast(ArrowNavigation.Direction.DOWN)
						lastNodePointLeft.down = uiportPoint
						uiportPoint.up = lastNodePointLeft
					}
				}
			}
		}

		// Then connect all the uiports, creating lines between them that user can follow using arrow keys
		val linesCreated = ArrayList<Line>()
		for (node in nodeView.nodes.values) {
			val nodePoint = pointMap.entries.first { it.value === node }.key

			for (uiportA in node.getPorts()) {
				val uiportPointA = pointMap.entries.first { it.value === uiportA }.key

				for (uiportB in nodeView.uiNet.getAllConnectedPorts(uiportA)) {
					if (linesCreated.any { (it.portA === uiportA && it.portB === uiportB) || (it.portB === uiportA && it.portA === uiportB) })
						continue // Already set up

					val uiportPointB = pointMap.entries.first { it.value === uiportB }.key




					if (linesCreated.any { (it.portA === uiportA && it.portB === uiportB) || (it.portB === uiportA && it.portA === uiportB) })
						continue // Already set up

					if (linesCreated.any { (it.portA === uiportA && it.portB === uiportB) || (it.portB === uiportA && it.portA === uiportB) })
						continue // Already set up







					val linePoint = arrowNavigation.newPoint()
					val line = Line(uiportA, uiportB)
					linesCreated.add(line)
					pointMap[linePoint] = line

					if (uiportA.output) {

					} else {

					}
				}
			}
		}
	}

	fun move(direction: ArrowNavigation.Direction) {
		TODO()
	}
}