package net.merayen.elastic.ui.objects.top.views.filebrowserview

import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.View
import java.io.File

class FileBrowserView : View() {
	private val bar = FileBrowserViewBar()
	private val fileList = FileList()

	override fun onInit() {
		super.onInit()
		add(fileList)
		fileList.browse("/")
		fileList.translation.x = 0f
		fileList.translation.y = 40f
		fileList.setHandler(object : FileList.Handler {
			override fun onSelect(file: File?) {
				if (file == null)
					fileList.up()
				else if (file.isDirectory)
					fileList.browse(file.absolutePath)
			}

		})

		add(bar)
	}

	override fun onUpdate() {
		fileList.layoutWidth = getWidth()
		fileList.layoutHeight = getHeight() - 40
	}

	override fun cloneView(): View {
		return FileBrowserView()
	}

	override val easyMotionBranch = object : Branch(this) {}
}
