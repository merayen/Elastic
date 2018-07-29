package net.merayen.elastic.ui.objects.top.views.filebrowserview

import net.merayen.elastic.ui.objects.components.dragdrop.PopupLabel
import java.io.File

class FileListItemDragable(val file: File) : PopupLabel("File ${file.name}")