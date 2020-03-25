package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.FilterInlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.uinodes.UINodeInformation

class AddNodeWindow : UIObject() {
	interface Handler {
		fun onClose()
		fun onSelect(node: BaseInfo)
	}

	private class ResultItem(val nodeInfo: BaseInfo) : UIObject() {

		init {
			add(Label(nodeInfo.name, eventTransparent = false))
		}
	}

	var handler: Handler? = null

	val filterInlineWindow = FilterInlineWindow()

	override fun onInit() {
		filterInlineWindow.handler = object : FilterInlineWindow.Handler {
			override fun onSearch(text: String) {
				search(text)
			}

			override fun onFocus(obj: UIObject) {}

			override fun onSelect(obj: UIObject) {
				select(obj as ResultItem)
			}

			override fun onClose() {
				handler?.onClose()
			}
		}

		translation.color.alpha = 0.7f
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

		val resultList = ArrayList<ResultItem>()

		for (nodeInfo in nodeInfos)
			if (text in nodeInfo.name.toLowerCase() || text in nodeInfo.description.toLowerCase())
				resultList.add(ResultItem(nodeInfo))

		filterInlineWindow.setResults(resultList)
	}

	private fun select(result: ResultItem) {
		handler?.onSelect(result.nodeInfo)
	}

	fun close() {
		filterInlineWindow.close()
	}
}