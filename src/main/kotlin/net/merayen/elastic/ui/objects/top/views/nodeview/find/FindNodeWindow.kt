package net.merayen.elastic.ui.objects.top.views.nodeview.find

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.FilterInlineWindow
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.uinodes.BaseInfo
import net.merayen.elastic.uinodes.UINodeInformation
import net.merayen.elastic.util.NodeUtil

class FindNodeWindow(private val netlist: NetList) : UIObject() {
	interface Handler {
		fun onClose()
		fun onSelect(nodeId: String)
	}

	private class ResultItem(val nodeId: String, val name: String) : UIObject() {
		init {
			add(Label(name, eventTransparent = false))
		}
	}

	var handler: Handler? = null

	val filterInlineWindow = FilterInlineWindow()

	override fun onInit() {
		filterInlineWindow.handler = object : FilterInlineWindow.Handler {
			override fun onSearch(text: String) {
				search(text)
			}

			override fun onSelect(uiobject: UIObject) {
				select(uiobject as ResultItem)
			}

			override fun onClose() {
				handler?.onClose()
			}
		}

		filterInlineWindow.title = "Find node"
		add(filterInlineWindow)
	}

	private var focused = false

	private fun search(inputText: String) {
		val text = inputText.toLowerCase()
		val properties = NodeProperties(netlist)

		val resultItems = ArrayList<ResultItem>()

		for (node in netlist.nodes) {
			val name = properties.getName(node)

			if (text in name.toLowerCase())
				resultItems.add(ResultItem(node.id, name))
		}

		resultItems.sortBy {
			val name = it.name.toLowerCase()
			if (name.startsWith(text))
				name
			else
				"\uFFFF${name}"
		}

		filterInlineWindow.setResults(resultItems)

		if (resultItems.isNotEmpty())
			filterInlineWindow.selected = resultItems[0]
	}

	private fun select(result: ResultItem) {
		handler?.onSelect(result.nodeId)
	}
}