package net.merayen.elastic.ui.event

class KeyboardEvent(surface_id: String, val character: Char, val code: Int, val action: Action) : UIEvent(surface_id) {
	enum class Action {
		DOWN,
		UP
	}
}
