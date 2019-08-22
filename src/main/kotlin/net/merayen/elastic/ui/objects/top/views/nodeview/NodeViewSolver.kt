package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.ui.objects.node.UINode

/**
 * Re-arranges nodes
 */
class NodeViewSolver(private val uinodes: Array<UINode>) {
	fun solve() {
		sillySolve()
	}

	private fun sillySolve() { // Temporary until we write the algorithm
		val nodes = getInputNodes()

		var inputRow = 0
		var outputRow = 0
		var looseRow = 0
		for (uinode in uinodes) { // Bullshit-sorter
			if (uinode in nodes.input) {
				uinode.targetLocation.x = 0f
				uinode.targetLocation.y = inputRow * 100f
				inputRow++
			} else if (uinode in nodes.output) {
				uinode.targetLocation.x = 800f
				uinode.targetLocation.y = outputRow * 100f
				outputRow++
			} else if (uinode in nodes.loose) {
				uinode.targetLocation.x = looseRow * 100f
				uinode.targetLocation.y = 800f
				looseRow++
			} else {
				uinode.targetLocation.x = (Math.random() * 300).toFloat() + 300
				uinode.targetLocation.y = (Math.random() * 300).toFloat() + 300
			}
		}
	}

	private class NodeArray(val input: ArrayList<UINode> = ArrayList(), val output: ArrayList<UINode> = ArrayList(), val loose: ArrayList<UINode> = ArrayList())

	private fun getInputNodes(): NodeArray {
		val result = NodeArray()

		for (uinode in uinodes) {
			val uinet = uinode.UINet

			if (uinet != null) {
				val ports = uinode.ports

				var isConnected = false
				var isOnlyInput = true
				var isOnlyOutput = true

				for (uiport in ports) {
					if (uinet.isConnected(uiport)) {
						isConnected = true
						if (uiport.output)
							isOnlyInput = false
						else
							isOnlyOutput = false
					}
				}

				if (isConnected) {
					if (isOnlyOutput)
						result.input.add(uinode)
					else
						result.output.add(uinode)
				} else {
					result.loose.add(uinode)
				}
			}
		}

		return result
	}
}