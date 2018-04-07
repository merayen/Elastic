package net.merayen.elastic.ui.objects.node

import net.merayen.elastic.ui.objects.nodeeditor.NodeEditor

/**
 * Apply this interface on your node to make it possible to edit the node in its own view, EditNodeView.
 */
interface INodeEditable {
	fun getNodeEditor(): NodeEditor
}