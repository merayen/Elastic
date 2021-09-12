package net.merayen.elastic.uinodes.list.xy_map_1

import net.merayen.elastic.backend.logicnodes.list.xy_map_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.curvebox.ACSignalBezierCurveBox
import net.merayen.elastic.ui.objects.components.curvebox.MapBezierCurveBox
import net.merayen.elastic.ui.objects.node.Resizable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.max
import kotlin.math.min

class UI : UINode() {
	private val bezierGraph = MapBezierCurveBox()

	override fun onInit() {
		super.onInit()
		layoutWidth = 200f
		layoutHeight = 300f

		titlebar.title = "XY Map"

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

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties

		// Restore node size, if any set, from project
		properties.layoutWidth?.let { layoutWidth = it }
		properties.layoutHeight?.let { layoutHeight = it }

		val curve = properties.curve
		if (curve != null)
			this.bezierGraph.setPoints(curve)
	}

	override fun onData(message: NodeDataMessage) {}

	private fun updateLayout() {
		getPort("out")?.translation?.x = layoutWidth
		getPort("fac")?.translation?.y = layoutHeight - 20f
		bezierGraph.layoutWidth = layoutWidth - 20f
		bezierGraph.layoutHeight = layoutHeight - 60f
	}
}