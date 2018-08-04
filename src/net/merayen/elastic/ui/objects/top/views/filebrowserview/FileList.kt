package net.merayen.elastic.ui.objects.top.views.filebrowserview

import java.io.File
import java.util.ArrayList

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class FileList : UIObject() {

	var layoutWidth = 100f
	var layoutHeight = 100f

	private val flist = FList()
	private val scroll = Scroll(flist)
	var files: Array<File>? = null
	private var path: String? = null
	private var handler: Handler? = null

	internal inner class FList internal constructor() : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox(10f)) {
		private var current_files: Array<File>? = null

		override fun onUpdate() {
			(placement as LayoutMethods.HorizontalBox).maxWidth = getWidth()

			if (files != current_files) {
				for (obj in ArrayList(search.children))
					remove(obj)

				add(object : FileListItem(File(".. (go back)"), false) {
					init {
						setHandler(object : FileListItem.Handler {
							override fun onClick() {
								if (handler != null)
									handler!!.onSelect(null)
							}
						})
					}
				})

				if (files != null) {
					for (f in files!!) {
						add(object : FileListItem(f, true) {
							init {
								setHandler(object : FileListItem.Handler {
									override fun onClick() {
										if (handler != null)
											handler!!.onSelect(f)
									}
								})
							}
						})
					}
				}

				current_files = files
			}

			super.onUpdate()
		}
	}

	internal interface Handler {
		fun onSelect(file: File?)
	}

	override fun onInit() {
		add(scroll)
	}

	override fun onUpdate() {
		scroll.layoutWidth = layoutWidth
		scroll.layoutHeight = layoutHeight
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(255, 0, 255)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	fun browse(path: String) {
		this.path = path
		files = File(path).listFiles()
	}

	fun setHandler(handler: Handler) {
		this.handler = handler
	}

	fun up() {
		val parent = File(path!!).parentFile
		if (parent != null)
			browse(parent!!.absolutePath)
	}
}
