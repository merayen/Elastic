package net.merayen.elastic.ui.objects.top.views.filebrowserview

import net.merayen.elastic.ui.objects.components.dragdrop.DragPopupLabel
import java.io.File

class FileListItemDragable(val file: File) : DragPopupLabel("File ${file.name}")