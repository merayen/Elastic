package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.FilterInlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.uinodes.UINodeInformation

class AddNodeWindow : UIObject() {
	interface Handler {
		fun onClose()
		fun onSelect(uiobject: UIObject)
	}

	var handler: Handler? = null

	val filterInlineWindow = FilterInlineWindow()

	override fun onInit() {
		filterInlineWindow.handler = object : FilterInlineWindow.Handler {
			override fun onSearch(text: String) {
				search(text)
			}

			override fun onSelect(uiobject: UIObject) {
				select(uiobject)
			}

			override fun onClose() {
				handler?.onClose()
			}
		}

		filterInlineWindow.title = "Add node"
		add(filterInlineWindow)
	}

	private var focused = false

	private fun search(text: String) {
		val text = text.toLowerCase()

		val nodeInfos = UINodeInformation.getNodeInfos()

		nodeInfos.filter {
			text in it.name.toLowerCase() || text in it.description.toLowerCase()
		}

		nodeInfos.sortBy {
			val name = it.name.toLowerCase()
			if (name.startsWith(text))
				name
			else
				"\uFFFF${name}"
		}

		val resultList = ArrayList<Label>()

		for (nodeInfo in nodeInfos)
			if (text in nodeInfo.name.toLowerCase() || text in nodeInfo.description.toLowerCase())
				resultList.add(Label(nodeInfo.name, eventTransparent = false))

		filterInlineWindow.setResults(resultList)
	}

	private fun select(uiobject: UIObject) {
		handler?.onSelect(uiobject)
	}
}