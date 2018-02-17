package net.merayen.elastic.ui.event

class FileDropEvent(surfaceID: String, x: Int, y: Int, action: Action, button: MouseEvent.Button, files: Array<String>) : MouseEvent(surfaceID, x, y, action, button)