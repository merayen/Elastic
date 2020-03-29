package net.merayen.elastic.ui.objects.components.framework

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

/**
 * Defines a port, and shows one view if that port is not connected, and shows another one if it is.
 * Doesn't show any UI itself, but add it to your UIObject to make it work.
 */
class PortParameter(private val node: UINode, private val port: UIPort, val not_connected: UIObject, val connected: UIObject) : UIObject() {

	private var last_port_check: Long = 0

	override fun onUpdate() {
		if (last_port_check + 100 < System.currentTimeMillis()) {
			last_port_check = System.currentTimeMillis()
			if (node.UINet!!.isConnected(port)) {
				if (connected.parent == null)
					add(connected)
				if (not_connected.parent != null)
					remove(not_connected)
			} else {
				if (connected.parent != null)
					remove(connected)
				if (not_connected.parent == null)
					add(not_connected)
			}
		}
	}
}
