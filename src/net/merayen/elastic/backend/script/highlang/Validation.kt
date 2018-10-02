package net.merayen.elastic.backend.script.highlang

class Validation(token: Token) {
	enum class ItemType {
		INFO, WARNING, ERROR
	}

	class Item(token: Token, message: String, type: ItemType)

	val items = ArrayList<Item>()
	private val lexerTraverse = LexerTraverse(token)

	init {
		checkVariables()
	}

	fun checkVariables() {
		//for ()
	}

	private fun warning(token: Token, message: String) {
		items.add(Item(token, message, ItemType.WARNING))
	}

	private fun error(token: Token, message: String) {
		items.add(Item(token, message, ItemType.ERROR))
	}
}