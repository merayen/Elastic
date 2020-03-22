package net.merayen.elastic.ui.objects.top.views.nodeview


import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.node.UIPortTemporary
import net.merayen.elastic.ui.util.ArrowNavigation

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

	var current: Any? = null
		get() = pointMap[arrowNavigation.current]
		set(value) {
			if (value != null && value !in pointMap.values)
				throw RuntimeException("Can not mark object $value, as it is not in pointMap")

			field = value
		}

	private val arrowNavigation = ArrowNavigation()

	private val pointMap = HashMap<ArrowNavigation.Point, Any>()

	private var nodeViewRevision = -1
	private var uiNetRevision = -1

	override fun onUpdate() {
		if (nodeView.revision == nodeViewRevision && nodeView.uiNet.revision == uiNetRevision)
			return

		rebuild()

		nodeViewRevision = nodeView.revision
		uiNetRevision = nodeView.uiNet.revision
	}

	override fun onDraw(draw: Draw) {
		val obj = current ?: return

		draw.disableOutline()

		when (obj) {
			is UINode -> {
				val pos = getRelativePosition(obj) ?: return
				val size = getRelativePosition(obj, obj.layoutWidth, obj.layoutHeight) ?: return
				size.x -= pos.x
				size.y -= pos.y

				draw.setColor(1f, 1f, 0.5f)
				draw.setStroke(2f)
				draw.rect(pos.x, pos.y, size.x, size.y)
			}
			is UIPort -> {
				val pos = getRelativePosition(obj) ?: return
				draw.setColor(1f, 1f, 0.5f)
				draw.setStroke(2f)
				draw.rect(pos.x - 5, pos.y - 5, 10f, 10f)
			}
			is Line -> {
				val posA = getRelativePosition(obj.portA) ?: return
				val posB = getRelativePosition(obj.portB) ?: return
				draw.setColor(1f, 1f, 0.5f)
				draw.setStroke(5f)
				draw.line(posA.x, posA.y, posB.x, posB.y)
			}
			else -> throw RuntimeException("Should not happen")
		}
	}

	/**
	 * Every time something has been changed, remember to call this method!
	 */
	fun rebuild() {
		pointMap.clear()
		arrowNavigation.clear()

		// Add nodes
		for (uinode in nodeView.nodes.values) {
			val nodePoint = arrowNavigation.newPoint()
			pointMap[nodePoint] = uinode

			// Add its ports and tie them to the node, so arrow keys can jump from node to ports
			for (uiport in uinode.getPorts()) {
				val uiportPoint = arrowNavigation.newPoint()
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
			for (uiportA in node.getPorts()) {
				if (uiportA is UIPortTemporary)
					continue

				for (uiportB in nodeView.uiNet.getAllConnectedPorts(uiportA)) {
					if (uiportB is UIPortTemporary)
						continue

					if (linesCreated.any { (it.portA === uiportA && it.portB === uiportB) || (it.portB === uiportA && it.portA === uiportB) })
						continue // Already set up

					val inPort = if (uiportA.output) uiportB else uiportA
					val outPort = if (uiportA.output) uiportA else uiportB

					val inPortPoint = pointMap.entries.first { it.value === inPort }.key
					val outPortPoint = pointMap.entries.first { it.value === outPort }.key

					val linePoint = arrowNavigation.newPoint()
					val line = Line(inPort, outPort)
					linesCreated.add(line)
					pointMap[linePoint] = line

					linePoint.left = outPortPoint
					linePoint.right = inPortPoint
					inPortPoint.left = linePoint

					// Attach linePoint to the ports, and other linePoints
					val outPortPointRight = outPortPoint.right
					if (outPortPointRight == null) {
						outPortPoint.right = linePoint
					} else {
						val bottomMostLinePoint = outPortPointRight.findLast(ArrowNavigation.Direction.DOWN)
						bottomMostLinePoint.down = linePoint
						linePoint.up = bottomMostLinePoint
					}
				}
			}
		}

		System.currentTimeMillis()
	}

	fun move(direction: ArrowNavigation.Direction) {
		val last = arrowNavigation.current
		arrowNavigation.move(direction)
	}
}