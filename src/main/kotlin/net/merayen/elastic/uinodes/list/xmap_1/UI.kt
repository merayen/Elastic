package net.merayen.elastic.uinodes.list.xmap_1

import net.merayen.elastic.backend.logicnodes.list.xmap_1.Properties
import net.merayen.elastic.backend.logicnodes.list.xmap_1.StateUpdateData
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.components.curvebox.MapBezierCurveBox
import net.merayen.elastic.ui.objects.node.Resizable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.max
import kotlin.math.min

class UI : UINode() {
	private val bezierGraph = MapBezierCurveBox()
	private var xPositions = FloatArray(0)

	override fun onInit() {
		super.onInit()
		layoutWidth = 200f
		layoutHeight = 200f

		titlebar.title = "X Map"

		bezierGraph.handler = object : MapBezierCurveBox.Handler {
			override fun onChange() {
				send(Properties(curve = bezierGraph.floats))
			}

			override fun onMove() {
				send(Properties(curve = bezierGraph.floats))
			}
		}

		bezierGraph.translation.x = 10f
		bezierGraph.translation.y = 20f
		add(bezierGraph)

		add(
			Resizable(
				this,
				object : Resizable.Handler {
					override fun onResize() {
						layoutWidth = max(100f, min(layoutWidth, 500f))
						layoutHeight = max(100f, min(layoutHeight, 500f))

						updateLayout()
						send(Properties(layoutWidth = layoutWidth, layoutHeight = layoutHeight)) // Remember the node size set
					}
				}
			)
		)

		updateLayout()
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> port.translation.y = 20f
			"out" -> port.translation.y = 20f
		}

		updateLayout()
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(0f, 1f, 0f)
		draw.setStroke(2f)
		draw.disableOutline()
		for (pos in xPositions) {
			if (pos >= 0f) {
				val x = bezierGraph.translation.x + max(0f, min(1f, pos)) * (bezierGraph.layoutWidth)
				draw.line(x, bezierGraph.translation.y, x, bezierGraph.translation.y + bezierGraph.layoutHeight)
			}
		}
		draw.enableOutline()
	}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties

		// Restore node size, if any set, from project
		properties.layoutWidth?.let { layoutWidth = it }
		properties.layoutHeight?.let { layoutHeight = it }

		val curve = properties.curve
		if (curve != null)
			this.bezierGraph.setPoints(curve)
	}

	override fun onData(message: NodeDataMessage) {
		if (message is StateUpdateData) {
			xPositions = message.positions.copyOf()
		}
	}

	private fun updateLayout() {
		getPort("out")?.translation?.x = layoutWidth
		getPort("fac")?.translation?.y = layoutHeight - 20f
		bezierGraph.layoutWidth = layoutWidth - 20f
		bezierGraph.layoutHeight = layoutHeight - 60f
	}
}