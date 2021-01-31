package net.merayen.elastic.ui.objects.top.viewbar

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementView
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView
import net.merayen.elastic.ui.objects.top.views.filebrowserview.FileBrowserView
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.ui.objects.top.views.statisticsview.StatisticsView
import net.merayen.elastic.ui.objects.top.views.transportview.TransportView
import kotlin.reflect.KClass

internal class ViewSelector(handler: Handler) : UIObject() {
	interface Handler {
		fun onSelect(cls: KClass<out View>)
	}

	private val dropDown = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {
			handler.onSelect(views.first { it.label == (selected as DropDownItem).label }.cls)
		}
	})

	class DropDownItem(val cls: KClass<out View>, val label: String) : DropDown.Item(Label(label), TextContextMenuItem(label))

	private val views = ArrayList<DropDownItem>()

	init {
		views.add(DropDownItem(NodeView::class, "Nodes"))
		views.add(DropDownItem(FileBrowserView::class, "Files"))
		views.add(DropDownItem(EditNodeView::class, "Edit"))
		views.add(DropDownItem(TransportView::class, "Transport"))
		views.add(DropDownItem(ArrangementView::class, "Arrangement"))
		views.add(DropDownItem(StatisticsView::class, "Statistics"))
	}

	override fun onInit() {
		dropDown.layoutHeight = 34f
		add(dropDown)

		for (item in views)
			dropDown.addMenuItem(item)
	}

	fun setViewClass(cls: KClass<out View>) {
		dropDown.setViewItem(views.first { it.cls == cls })
	}

	override fun getWidth() = dropDown.getWidth()
	override fun getHeight() = dropDown.getHeight()
}
