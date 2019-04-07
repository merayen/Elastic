package net.merayen.elastic.ui.objects.top

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.dialogs.AboutDialog
import net.merayen.elastic.ui.objects.top.menu.Bar
import net.merayen.elastic.ui.objects.top.menu.MenuBarItem
import net.merayen.elastic.ui.objects.top.menu.MenuListItem

class MenuBar : UIObject(), FlexibleDimension {

	private val bar = Bar() // The bar on top
	private var handler: Handler? = null
	override var layoutWidth = 200f
	override var layoutHeight = 20f

	abstract class Handler {
		fun onOpenProject(path: String) {}
		fun onMakeCheckpoint() {}
		fun onSaveProjectAs() {}
		fun onClose() {}
	}

	override fun onInit() {
		add(bar)
		fillMenuBar()
	}

	override fun onUpdate() {
		bar.width = layoutWidth
	}

	private fun fillMenuBar() {
		val file = MenuBarItem("File")
		bar.addMenuBarItem(file)

		val new_project = MenuListItem("New project", MenuListItem.Handler { })
		file.menu_list.addMenuItem(new_project)

		val open = MenuListItem("Open...", MenuListItem.Handler {
			val fd = java.awt.FileDialog(null as java.awt.Frame?)
			fd.mode = java.awt.FileDialog.LOAD
			fd.title = "Choose a project"
			fd.isVisible = true
			if (fd.file != null && handler != null) {
				val path = fd.directory + fd.file
				handler!!.onOpenProject(path)
			}
		})
		file.menu_list.addMenuItem(open)

		val checkpoint = MenuListItem("Make checkpoint", MenuListItem.Handler {
			if (handler != null)
				handler!!.onMakeCheckpoint()
		})
		file.menu_list.addMenuItem(checkpoint)

		val save_as = MenuListItem("Save as...", MenuListItem.Handler { })
		file.menu_list.addMenuItem(save_as)

		val close = MenuListItem("Close project", MenuListItem.Handler {
			if (handler != null)
				handler!!.onClose()
		})
		file.menu_list.addMenuItem(close)


		val edit = MenuBarItem("Edit")
		bar.addMenuBarItem(edit)

		val undo = MenuListItem("Undo", MenuListItem.Handler { })
		edit.menu_list.addMenuItem(undo)

		val hilfe = MenuBarItem("Hilfe")
		bar.addMenuBarItem(hilfe)

		val about = MenuListItem("About Elastic", MenuListItem.Handler { add(AboutDialog()) })
		hilfe.menu_list.addMenuItem(about)
	}

	fun setHandler(handler: Handler) {
		this.handler = handler
	}
}
