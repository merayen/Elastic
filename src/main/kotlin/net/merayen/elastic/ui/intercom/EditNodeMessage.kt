package net.merayen.elastic.ui.intercom

import net.merayen.elastic.ui.objects.node.INodeEditable
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView

/**
 * Asks ViewportController to start editing a node.
 * Message is picked up by ViewportController and handled there.
 */
class EditNodeMessage(val node: INodeEditable, val targetView: EditNodeView) : UIMessage()